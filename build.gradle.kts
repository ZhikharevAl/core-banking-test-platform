plugins {
    java
    checkstyle
    jacoco
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

checkstyle {
    toolVersion = "10.17.0"
    configDirectory.set(file("config/checkstyle"))
    isIgnoreFailures = false
    maxWarnings = 0
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-suite-engine")
    testImplementation("io.cucumber:cucumber-java:7.34.3")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.34.3")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")
    testImplementation("io.qameta.allure:allure-cucumber7-jvm:2.32.0")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }

    systemProperty("cucumber.junit-platform.naming-strategy", "long")

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)

        html.required.set(true)
        html.outputLocation.set(
            layout.buildDirectory.dir("jacocoHtml")
        )
    }
}
