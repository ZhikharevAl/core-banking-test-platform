package org.example.weather.support;

import io.cucumber.java.DataTableType;
import java.util.Map;
import org.example.weather.fixtures.ApiErrorFixture;
import org.example.weather.fixtures.CurrentWeatherFixture;
import org.example.weather.fixtures.ErrorRequestCase;
import org.example.weather.fixtures.ExpectedApiError;

/**
 * Конвертеры строк Cucumber DataTable в типизированные фикстуры.
 */
public class DataTableTypes {

    @DataTableType
    public CurrentWeatherFixture currentWeatherFixture(final Map<String, String> row) {
        return new CurrentWeatherFixture(
                row.get("city"),
                Double.parseDouble(row.get("tempC")),
                Integer.parseInt(row.get("humidity")),
                row.get("condition")
        );
    }

    @DataTableType
    public ApiErrorFixture apiErrorFixture(final Map<String, String> row) {
        return new ApiErrorFixture(
                row.get("city"),
                Integer.parseInt(row.get("status")),
                Integer.parseInt(row.get("code")),
                row.get("message")
        );
    }

    @DataTableType
    public ErrorRequestCase errorRequestCase(final Map<String, String> row) {
        return new ErrorRequestCase(
                row.get("city"),
                row.get("code")
        );
    }

    @DataTableType
    public ExpectedApiError expectedApiError(final Map<String, String> row) {
        return new ExpectedApiError(
                Integer.parseInt(row.get("status")),
                Integer.parseInt(row.get("code")),
                row.get("message")
        );
    }

}
