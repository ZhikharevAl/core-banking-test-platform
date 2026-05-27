package org.example.weather.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import org.example.weather.api.WeatherEndpoint;
import org.example.weather.config.WeatherTestConfig;
import org.example.weather.fixtures.ApiErrorFixture;
import org.example.weather.fixtures.CurrentWeatherFixture;

/**
 * Сборщик WireMock-стабов.
 */
public class WeatherStubs {

    private final WireMockServerHolder holder;

    public WeatherStubs(final WireMockServerHolder holder) {
        this.holder = holder;
    }

    /**
     * Замокать успешный ответ на /v1/current.json для заданной фикстуры.
     */
    public void stubCurrentSuccess(final CurrentWeatherFixture fixture) {
        final String body = """
                {
                  "location": {"name": "%s"},
                  "current": {
                    "temp_c": %s,
                    "humidity": %d,
                    "condition": {"text": "%s"}
                  }
                }
                """.formatted(fixture.city(), fixture.tempC(), fixture.humidity(), fixture.condition());

        holder.server().stubFor(matchCurrentFor(fixture.city())
                .willReturn(okJson(body)));
    }

    /**
     * Замокать ошибочный ответ на /v1/current.json для конкретного значения q.
     * Подходит для 400/401/403 структура тела одинакова.
     */
    public void stubCurrentError(final ApiErrorFixture fixture) {
        final String body = """
                {"code": %d, "message": "%s"}
                """.formatted(fixture.errorCode(), escape(fixture.errorMessage()));

        holder.server().stubFor(matchCurrentFor(fixture.city())
                .willReturn(aResponse()
                        .withStatus(fixture.httpStatus())
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    private MappingBuilder matchCurrentFor(final String city) {
        return get(urlPathEqualTo(WeatherEndpoint.CURRENT.path()))
                .withQueryParam("key", equalTo(WeatherTestConfig.API_KEY))
                .withQueryParam("q", equalTo(city));
    }

    private String escape(final String raw) {
        return raw == null ? "" : raw.replace("\"", "\\\"");
    }

}
