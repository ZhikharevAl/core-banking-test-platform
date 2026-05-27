package org.example.weather;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class WeatherTestContext {
    private final Map<String, HttpResponse<String>> responsesByCity = new HashMap<>();
    private final Map<String, HttpResponse<String>> responsesByErrorCode = new HashMap<>();

    public Map<String, HttpResponse<String>> getResponsesByCity() {
        return responsesByCity;
    }

    public Map<String, HttpResponse<String>> getResponsesByErrorCode() {
        return responsesByErrorCode;
    }

}
