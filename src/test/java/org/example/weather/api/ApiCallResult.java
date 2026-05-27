package org.example.weather.api;

import java.net.http.HttpResponse;

/**
 * Результат HTTP-вызова. Хранит сырой ответ и URL.
 */
public record ApiCallResult(String url, HttpResponse<String> response) {

    public int statusCode() {
        return response.statusCode();
    }

    public String body() {
        return response.body();
    }

}
