package org.example.weather.support;

import java.util.LinkedHashMap;
import java.util.Map;
import org.example.weather.api.ApiCallResult;

/**
 * Контекст шагов одного сценария: сохраняет результаты вызовов
 * по ключу (имя города / код ошибки), чтобы Then-шаги могли разобрать их.
 * Allure записи идут в той же последовательности, что и в feature-файле.
 */
public class WeatherTestContext {

    private final Map<String, ApiCallResult> callsByCity = new LinkedHashMap<>();
    private final Map<String, ApiCallResult> callsByErrorCode = new LinkedHashMap<>();

    public void recordByCity(final String city, final ApiCallResult result) {
        callsByCity.put(city, result);
    }

    public void recordByErrorCode(final String code, final ApiCallResult result) {
        callsByErrorCode.put(code, result);
    }

    public ApiCallResult callByCity(final String city) {
        final ApiCallResult result = callsByCity.get(city);
        if (result == null) {
            throw new IllegalStateException("No recorded call for city='" + city + "'");
        }
        return result;
    }

    public ApiCallResult callByErrorCode(final String code) {
        final ApiCallResult result = callsByErrorCode.get(code);
        if (result == null) {
            throw new IllegalStateException("No recorded call for error code='" + code + "'");
        }
        return result;
    }

}
