package org.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Корневой объект ответа /v1/current.json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherResponse(Location location, Current current) {
}
