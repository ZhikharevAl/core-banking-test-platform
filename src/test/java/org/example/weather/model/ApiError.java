package org.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Тело ошибочного ответа Weather API. Структура одинакова для 400/401/403:
 * {@code {"code": ..., "message": "..."}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(int code, String message) {
}
