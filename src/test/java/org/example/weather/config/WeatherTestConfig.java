package org.example.weather.config;

/**
 * Тестовые константы окружения Weather API.
 */
public final class WeatherTestConfig {

    /** Общий ключ для стаба. */
    public static final String API_KEY = "test-api-key"; // pragma: allowlist secret

    /** Connection/read timeout HTTP-клиента. */
    public static final int HTTP_TIMEOUT_SECONDS = 10;

    private WeatherTestConfig() {
    }

}
