package org.example.weather.model;

/**
 * Текстовое описание погоды.
 */
public record Condition(String text, String icon, int code) {
}
