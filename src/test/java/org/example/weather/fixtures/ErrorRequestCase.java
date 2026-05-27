package org.example.weather.fixtures;

/**
 * Пара (город, код ошибки) для When-шага негативного сценария
 */
public record ErrorRequestCase(String city, String errorCode) {
}
