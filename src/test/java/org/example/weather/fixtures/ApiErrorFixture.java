package org.example.weather.fixtures;

/**
 * Описание стаба ошибки: какой статус-код вернуть, какое тело,
 * и по какому значению query-параметра q его триггерить.
 */
public record ApiErrorFixture(String city, int httpStatus, int errorCode, String errorMessage) {
}
