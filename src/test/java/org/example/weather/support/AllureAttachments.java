package org.example.weather.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import java.net.http.HttpResponse;
import org.example.weather.api.ApiCallResult;

/**
 * Утилита для полезной нагрузки к Allure-отчёту:
 * URL запроса и тело ответа.
 */
public final class AllureAttachments {

    private static final ObjectMapper PRETTY = new ObjectMapper();

    private AllureAttachments() {
    }

    public static void attachRequest(final String url) {
        Allure.addAttachment("Request", "text/plain", "GET " + url, ".txt");
    }

    public static void attachResponse(final HttpResponse<String> response) {
        final String content = "Status: " + response.statusCode() + System.lineSeparator()
                + System.lineSeparator() + prettyJson(response.body());
        Allure.addAttachment("Response", "application/json", content, ".json");
    }

    private static String prettyJson(final String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        try {
            return PRETTY.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(PRETTY.readTree(raw));
        } catch (JsonProcessingException ex) {
            return raw;
        }
    }

}
