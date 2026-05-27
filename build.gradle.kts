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
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }

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
