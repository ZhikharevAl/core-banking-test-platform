package org.example.weather.wiremock;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;

/**
 * Контейнер для WireMockServer
 */
public class WireMockServerHolder {

    private WireMockServer server;

    public void start() {
        if (server != null && server.isRunning()) {
            return;
        }
        server = new WireMockServer(options().dynamicPort());
        server.start();
    }

    public void stop() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
        server = null;
    }

    public WireMockServer server() {
        if (server == null || !server.isRunning()) {
            throw new IllegalStateException(
                    "WireMock server is not started. Did the @Before hook run?");
        }
        return server;
    }

    public String baseUrl() {
        return server().baseUrl();
    }

}
