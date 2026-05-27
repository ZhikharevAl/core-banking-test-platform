package org.example.weather;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber-сюита для Weather API.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/weather")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.example.weather")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
)
public class CucumberTestRunnerTests {

}
