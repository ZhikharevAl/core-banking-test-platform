package org.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Текущие показания: температура (Цельсий и Фаренгейт), влажность, условие.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Current(
        double tempC,
        double tempF,
        int humidity,
        Condition condition
) {
}
