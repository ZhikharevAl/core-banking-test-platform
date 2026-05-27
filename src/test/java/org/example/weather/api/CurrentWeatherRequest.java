package org.example.weather.api;

import org.example.weather.config.WeatherTestConfig;

public record CurrentWeatherRequest(String apiKey, String city, String lang) {

    public CurrentWeatherRequest {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey must not be null");
        }
        if (city == null) {
            throw new IllegalArgumentException("city must not be null");
        }
    }

    /**
     * Стандартный запрос — ключ берётся из {@link WeatherTestConfig}, lang не задан.
     */
    public static CurrentWeatherRequest forCity(final String city) {
        return new CurrentWeatherRequest(WeatherTestConfig.API_KEY, city, null);
    }

    /**
     * Запрос с явным ключом — для негативных сценариев.
     */
    public static CurrentWeatherRequest forCityWithKey(final String city, final String apiKey) {
        return new CurrentWeatherRequest(apiKey, city, null);
    }

}
