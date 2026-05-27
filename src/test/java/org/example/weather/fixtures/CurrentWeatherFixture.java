package org.example.weather.fixtures;

/**
 * Иммутабельный набор данных для стаба ответа /v1/current.json.
 * Используется только в тестах задаёт как должен ответить WireMock.
 * Поля совпадают с теми, что в DataTable feature-файлов.
 */
public record CurrentWeatherFixture(String city, double tempC, int humidity, String condition) {
}
