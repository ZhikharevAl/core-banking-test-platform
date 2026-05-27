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

    testImplementation("org.assertj:assertj-core:3.26.3")

    testImplementation("io.cucumber:cucumber-java:7.34.3")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.34.3")

    testImplementation("io.cucumber:cucumber-picocontainer:7.34.3")

    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")
    testImplementation("io.qameta.allure:allure-cucumber7-jvm:2.32.0")
}

tasks.register("writeAllureEnvironment") {
    val outputFile = layout.buildDirectory.file("allure-results/environment.properties")
    outputs.file(outputFile)
    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            Project=core-banking-test-platform
            Module=weather-api-tests
            Java.Version=${System.getProperty("java.version")}
            Gradle.Version=${gradle.gradleVersion}
            OS=${System.getProperty("os.name")} ${System.getProperty("os.version")}
            Cucumber=7.34.3
            WireMock=3.0.1
            AssertJ=3.26.3
            Allure=2.32.0
            Jackson=2.21.2
            """.trimIndent()
        )
    }
}

tasks.register<Copy>("copyAllureCategories") {
    from("src/test/resources/allure/categories.json")
    into(layout.buildDirectory.dir("allure-results"))
}

tasks.test {
    dependsOn("writeAllureEnvironment", "copyAllureCategories")
    useJUnitPlatform()

    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }

    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    systemProperty(
        "allure.results.directory",
        layout.buildDirectory.dir("allure-results").get().asFile.absolutePath
    )

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
