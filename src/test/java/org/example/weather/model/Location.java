package org.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Локация из ответа Weather API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(String name, String region, String country) {
}
