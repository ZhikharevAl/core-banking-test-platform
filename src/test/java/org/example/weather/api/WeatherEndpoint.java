package org.example.weather.api;

/**
 * Список endpoint-ов Weather API.
 */
public enum WeatherEndpoint {
    CURRENT("/v1/current.json");

    private final String path;

    WeatherEndpoint(final String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
