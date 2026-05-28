package org.example.weather.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.example.weather.api.ApiCallResult;
import org.example.weather.api.CurrentWeatherRequest;
import org.example.weather.api.WeatherApiClient;
import org.example.weather.api.WeatherResponseParser;
import org.example.weather.fixtures.CurrentWeatherFixture;
import org.example.weather.model.CurrentWeatherResponse;
import org.example.weather.support.Mismatches;
import org.example.weather.support.WeatherTestContext;
import org.example.weather.wiremock.WeatherStubs;

/**
 * Степы для позитивного сценария.
 */
public class CurrentWeatherSteps {

    private final WeatherStubs stubs;
    private final WeatherApiClient client;
    private final WeatherTestContext context;

    public CurrentWeatherSteps(
            final WeatherStubs stubs,
            final WeatherApiClient client,
            final WeatherTestContext context
    ) {
        this.stubs = stubs;
        this.client = client;
        this.context = context;
    }

    @Given("mock-сервис погоды содержит корректные ответы для городов")
    public void mockWeatherServiceHasValidResponsesForCities(final List<CurrentWeatherFixture> fixtures) {
        fixtures.forEach(stubs::stubCurrentSuccess);
    }

    @When("я запрашиваю текущую погоду для городов")
    public void requestCurrentWeatherForCities(final List<String> cities) {
        for (String city : cities) {
            final ApiCallResult result = client.getCurrent(CurrentWeatherRequest.forCity(city));
            context.recordByCity(city, result);
        }
    }

    @Then("значения ответа совпадают с ожидаемыми")
    public void weatherResponseMatchesExpectedValues(final List<CurrentWeatherFixture> expected) {
        SoftAssertions.assertSoftly(softly -> {
            for (CurrentWeatherFixture row : expected) {
                final ApiCallResult result = context.callByCity(row.city());
                final CurrentWeatherResponse parsed = WeatherResponseParser.parseCurrent(result);

                Allure.step("Verify " + row.city() + ": "
                        + "tempC=" + parsed.current().tempC()
                        + ", humidity=" + parsed.current().humidity()
                        + ", condition=" + parsed.current().condition().text());

                Mismatches.report(row.city(), "tempC", row.tempC(), parsed.current().tempC());
                Mismatches.report(row.city(), "humidity", row.humidity(), parsed.current().humidity());
                Mismatches.report(row.city(), "condition", row.condition(), parsed.current().condition().text());

                softly.assertThat(result.statusCode())
                        .as("HTTP status for %s", row.city())
                        .isEqualTo(200);
                softly.assertThat(parsed.current().tempC())
                        .as("tempC for %s", row.city())
                        .isEqualTo(row.tempC());
                softly.assertThat(parsed.current().humidity())
                        .as("humidity for %s", row.city())
                        .isEqualTo(row.humidity());
                softly.assertThat(parsed.current().condition().text())
                        .as("condition for %s", row.city())
                        .isEqualTo(row.condition());
            }
        });
    }

    @Then("расхождения с ожидаемыми значениями зафиксированы в логе")
    public void mismatchesAreRecordedInLog(final List<CurrentWeatherFixture> expected) {
        final List<String> mismatches = new ArrayList<>();
        for (CurrentWeatherFixture row : expected) {
            final ApiCallResult result = context.callByCity(row.city());
            final CurrentWeatherResponse parsed = WeatherResponseParser.parseCurrent(result);

            if (Mismatches.report(row.city(), "tempC", row.tempC(), parsed.current().tempC())) {
                mismatches.add(row.city() + ".tempC");
            }
            if (Mismatches.report(row.city(), "humidity", row.humidity(), parsed.current().humidity())) {
                mismatches.add(row.city() + ".humidity");
            }
            if (Mismatches.report(row.city(), "condition", row.condition(), parsed.current().condition().text())) {
                mismatches.add(row.city() + ".condition");
            }
        }
        Assertions.assertThat(mismatches)
                .as("Ожидались зафиксированные расхождения по значениям")
                .isNotEmpty();
    }

}
