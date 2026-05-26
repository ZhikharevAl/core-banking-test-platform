package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Sanity-проверка инфраструктуры: компиляция, JUnit Platform, checkstyle.
 */
class SanityTests {

    @Test
    void buildIsHealthyTest() {
        assertEquals(2, 1 + 1, "infrastructure is wired up correctly");
    }
}
