package org.example.weather.api;

import java.io.Serial;

/**
 * Доменное исключение клиента.
 */
public class WeatherApiClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public WeatherApiClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WeatherApiClientException(final String message) {
        super(message);
    }

}
