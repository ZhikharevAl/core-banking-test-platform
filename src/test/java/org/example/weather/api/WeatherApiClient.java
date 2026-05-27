package org.example.weather.api;

import io.qameta.allure.Step;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.example.weather.config.WeatherTestConfig;
import org.example.weather.support.AllureAttachments;
import org.example.weather.wiremock.WireMockServerHolder;

/**
 * Тонкий HTTP-клиент. Делает ровно одно: собирает URI, отправляет GET,
 * фиксирует запрос/ответ в Allure и отдаёт сырой {@link ApiCallResult}.
 */
public class WeatherApiClient {

    private final WireMockServerHolder serverHolder;
    private final HttpClient httpClient;
    private final Duration timeout;

    public WeatherApiClient(final WireMockServerHolder serverHolder) {
        this.serverHolder = serverHolder;
        this.timeout = Duration.ofSeconds(WeatherTestConfig.HTTP_TIMEOUT_SECONDS);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    @Step("GET /v1/current.json?q={request.city}")
    public ApiCallResult getCurrent(final CurrentWeatherRequest request) {
        final String url = buildCurrentUrl(request);
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET()
                .build();

        AllureAttachments.attachRequest(url);
        try {
            final HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            AllureAttachments.attachResponse(response);
            return new ApiCallResult(url, response);
        } catch (IOException ex) {
            throw new WeatherApiClientException("HTTP call failed: " + url, ex);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new WeatherApiClientException("HTTP call interrupted: " + url, ex);
        }
    }

    private String buildCurrentUrl(final CurrentWeatherRequest request) {
        final StringBuilder url = new StringBuilder(serverHolder.baseUrl())
                .append(WeatherEndpoint.CURRENT.path())
                .append("?key=").append(encode(request.apiKey()))
                .append("&q=").append(encode(request.city()));
        if (request.lang() != null && !request.lang().isBlank()) {
            url.append("&lang=").append(encode(request.lang()));
        }
        return url.toString();
    }

    private String encode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
