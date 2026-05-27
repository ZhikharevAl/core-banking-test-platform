package org.example.weather.api;

/**
 * Список endpoint-ов Weather API.
 */
public enum WeatherEndpoint {
    CURRENT("/v1/current.json"),
    FORECAST("/v1/forecast.json"),
    HISTORY("/v1/history.json"),
    FUTURE("/v1/future.json");

    private final String path;

    WeatherEndpoint(final String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
