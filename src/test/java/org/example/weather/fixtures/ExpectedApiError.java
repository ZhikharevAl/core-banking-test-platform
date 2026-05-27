package org.example.weather.fixtures;

/**
 * Ожидание в Then-шаге негативного сценария: какие HTTP-status,
 * error.code и error.message должен вернуть API.
 */
public record ExpectedApiError(int httpStatus, int errorCode, String errorMessage) {
}
