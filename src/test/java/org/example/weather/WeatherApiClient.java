package org.example.weather;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WeatherApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;

    public WeatherApiClient(final String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> currentWeather(final String apiKey, final String city)
            throws IOException, InterruptedException {
        final String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        final String uri = String.format("%s/v1/current.json?key=%s&q=%s", baseUrl, apiKey, encodedCity);

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
