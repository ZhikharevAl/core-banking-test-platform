package org.example.weather.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.example.weather.wiremock.WireMockServerHolder;

/**
 * Жизненный цикл WireMock на каждый сценарий.
 */
public class WireMockHooks {

    private final WireMockServerHolder serverHolder;

    public WireMockHooks(final WireMockServerHolder serverHolder) {
        this.serverHolder = serverHolder;
    }

    @Before
    public void startWireMock() {
        serverHolder.start();
    }

    @After
    public void stopWireMock() {
        serverHolder.stop();
    }

}
