package org.example.weather.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.example.weather.api.ApiCallResult;
import org.example.weather.api.CurrentWeatherRequest;
import org.example.weather.api.WeatherApiClient;
import org.example.weather.api.WeatherResponseParser;
import org.example.weather.fixtures.ApiErrorFixture;
import org.example.weather.fixtures.ErrorRequestCase;
import org.example.weather.fixtures.ExpectedApiError;
import org.example.weather.model.ApiError;
import org.example.weather.support.Mismatches;
import org.example.weather.support.WeatherTestContext;
import org.example.weather.wiremock.WeatherStubs;

/**
 * Степы для негативных сценариев. Одинаково покрывают 400 / 401 / 403.
 */
public class WeatherErrorSteps {

    private final WeatherStubs stubs;
    private final WeatherApiClient client;
    private final WeatherTestContext context;

    public WeatherErrorSteps(
            final WeatherStubs stubs,
            final WeatherApiClient client,
            final WeatherTestContext context
    ) {
        this.stubs = stubs;
        this.client = client;
        this.context = context;
    }

    @Given("mock-сервис погоды возвращает ответы с ошибками")
    public void mockWeatherServiceReturnsErrorResponses(final List<ApiErrorFixture> fixtures) {
        fixtures.forEach(stubs::stubCurrentError);
    }

    @When("я запрашиваю погоду для некорректных входных данных")
    public void requestWeatherWithInvalidInputCities(final List<ErrorRequestCase> cases) {
        for (ErrorRequestCase requestCase : cases) {
            final ApiCallResult result = client.getCurrent(
                    CurrentWeatherRequest.forCity(requestCase.city()));
            context.recordByErrorCode(requestCase.errorCode(), result);
        }
    }

    @Then("ответ с ошибкой совпадает с ожидаемым")
    public void errorResponseMatchesExpectedApiErrors(final List<ExpectedApiError> expected) {
        SoftAssertions.assertSoftly(softly -> {
            for (ExpectedApiError row : expected) {
                final String code = String.valueOf(row.errorCode());
                final ApiCallResult result = context.callByErrorCode(code);
                final ApiError parsed = WeatherResponseParser.parseError(result);

                Allure.step("Verify error " + code + ": status="
                        + result.statusCode() + ", message='" + parsed.message() + "'");

                Mismatches.report(code, "httpStatus", row.httpStatus(), result.statusCode());
                Mismatches.report(code, "errorCode", row.errorCode(), parsed.code());
                Mismatches.report(code, "errorMessage", row.errorMessage(), parsed.message());

                softly.assertThat(result.statusCode())
                        .as("HTTP status for error %s", code)
                        .isEqualTo(row.httpStatus());
                softly.assertThat(parsed.code())
                        .as("error code in body for %s", code)
                        .isEqualTo(row.errorCode());
                softly.assertThat(parsed.message())
                        .as("error message for %s", code)
                        .isEqualTo(row.errorMessage());
            }
        });
    }

}
