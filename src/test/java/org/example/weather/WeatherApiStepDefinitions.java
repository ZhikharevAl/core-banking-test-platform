package org.example.weather;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public class WeatherApiStepDefinitions {
    private static final String API_KEY = System.getProperty("weather.api.key", "PUT_YOUR_REAL_WEATHERAPI_KEY_HERE");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WeatherTestContext context = new WeatherTestContext();
    private WireMockServer wireMockServer;
    private WeatherApiClient weatherApiClient;

    @Before
    public void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        weatherApiClient = new WeatherApiClient(wireMockServer.baseUrl());
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }

    @Given("mock weather service has valid responses for cities")
    public void mockWeatherServiceHasValidResponsesForCities(final List<Map<String, String>> rows) {
        rows.forEach(row -> {
            final String city = normalizeCity(row.get("city"));
            final String tempC = row.get("tempC");
            final String humidity = row.get("humidity");
            final String condition = row.get("condition");

            final String body = String.format(
                    """
                        {
                          \"location\": {\"name\": \"%s\"},
                          \"current\": {
                            \"temp_c\": %s,
                            \"humidity\": %s,
                            \"condition\": {\"text\": \"%s\"}
                          }
                        }
                        """,
                    city,
                    tempC,
                    humidity,
                    condition
            );

            wireMockServer.stubFor(get(urlPathEqualTo("/v1/current.json"))
                    .withQueryParam("key", com.github.tomakehurst.wiremock.client.WireMock.equalTo(API_KEY))
                    .withQueryParam("q", com.github.tomakehurst.wiremock.client.WireMock.equalTo(city))
                    .willReturn(okJson(body)));
        });
    }

    @When("I request current weather for cities")
    public void requestCurrentWeatherForCities(final List<String> cities) throws IOException, InterruptedException {
        for (String city : cities) {
            final HttpResponse<String> response = weatherApiClient.currentWeather(API_KEY, city);
            context.getResponsesByCity().put(city, response);
        }
    }

    @Then("weather response matches expected values")
    public void weatherResponseMatchesExpectedValues(final List<Map<String, String>> expectedRows) throws IOException {
        for (Map<String, String> expected : expectedRows) {
            final String city = expected.get("city");
            final JsonNode json = objectMapper.readTree(context.getResponsesByCity().get(city).body());

            final String expectedCondition = expected.get("condition");
            final String actualCondition = json.at("/current/condition/text").asText();
            final double expectedTemp = Double.parseDouble(expected.get("tempC"));
            final double actualTemp = json.at("/current/temp_c").asDouble();
            final int expectedHumidity = Integer.parseInt(expected.get("humidity"));
            final int actualHumidity = json.at("/current/humidity").asInt();

            logDifference(city, "condition", expectedCondition, actualCondition);
            logDifference(city, "tempC", expectedTemp, actualTemp);
            logDifference(city, "humidity", expectedHumidity, actualHumidity);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedCondition, actualCondition),
                    () -> Assertions.assertEquals(expectedTemp, actualTemp),
                    () -> Assertions.assertEquals(expectedHumidity, actualHumidity)
            );
        }
    }

    @Given("mock weather service has error responses")
    public void mockWeatherServiceHasErrorResponses(final List<Map<String, String>> rows) {
        rows.forEach(row -> {
            final String city = normalizeCity(row.get("city"));
            final String code = row.get("code");
            final String message = row.get("message");

            final String body = String.format("{\"code\":%s,\"message\":\"%s\"}", code, message);

            wireMockServer.stubFor(get(urlPathEqualTo("/v1/current.json"))
                    .withQueryParam("key", com.github.tomakehurst.wiremock.client.WireMock.equalTo(API_KEY))
                    .withQueryParam("q", com.github.tomakehurst.wiremock.client.WireMock.equalTo(city))
                    .willReturn(aResponse()
                            .withStatus(Integer.parseInt(row.get("status")))
                            .withHeader("Content-Type", "application/json")
                            .withBody(body)));
        });
    }

    @When("I request weather with invalid input cities")
    public void requestWeatherWithInvalidInputCities(final List<Map<String, String>> rows)
            throws IOException, InterruptedException {
        for (Map<String, String> row : rows) {
            final String city = normalizeCity(row.get("city"));
            final String code = row.get("code");
            context.getResponsesByErrorCode().put(code, weatherApiClient.currentWeather(API_KEY, city));
        }
    }

    @Then("error response matches expected api errors")
    public void errorResponseMatchesExpectedApiErrors(final List<Map<String, String>> expectedRows) throws IOException {
        for (Map<String, String> expected : expectedRows) {
            final String code = expected.get("code");
            final String expectedMessage = expected.get("message");

            final HttpResponse<String> response = context.getResponsesByErrorCode().get(code);
            final JsonNode json = objectMapper.readTree(response.body());
            final String actualMessage = json.at("/message").asText();
            final String actualCode = String.valueOf(json.at("/code").asInt());
            final int expectedStatus = Integer.parseInt(expected.get("status"));

            logDifference("error", "code", code, actualCode);
            logDifference("error", "message", expectedMessage, actualMessage);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedStatus, response.statusCode()),
                    () -> Assertions.assertEquals(code, actualCode),
                    () -> Assertions.assertEquals(expectedMessage, actualMessage)
            );
        }
    }

    private String normalizeCity(final String city) {
        return city == null ? "" : city;
    }

    private void logDifference(final String entity, final String field, final Object expected, final Object actual) {
        final String message = expected.equals(actual)
                ? String.format("[%s] %s matched: %s", entity, field, actual)
                : String.format("[%s] %s mismatch. expected=%s actual=%s", entity, field, expected, actual);
        Allure.step(message);
    }

}
