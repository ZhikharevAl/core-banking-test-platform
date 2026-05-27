package org.example.weather.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.example.weather.model.ApiError;
import org.example.weather.model.CurrentWeatherResponse;

/**
 * Отдельный слой парсинга. Клиент возвращает {@link ApiCallResult} (сырой ответ),
 * а парсер превращает тело в типизированные DTO.
 */
public final class WeatherResponseParser {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private WeatherResponseParser() {
    }

    public static CurrentWeatherResponse parseCurrent(final ApiCallResult result) {
        return read(result.body(), CurrentWeatherResponse.class);
    }

    public static ApiError parseError(final ApiCallResult result) {
        return read(result.body(), ApiError.class);
    }

    private static <T> T read(final String body, final Class<T> type) {
        try {
            return MAPPER.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new WeatherApiClientException(
                    "Failed to parse response body as " + type.getSimpleName() + ": " + body, e);
        }
    }

}
