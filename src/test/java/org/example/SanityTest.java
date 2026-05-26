package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanityTest {

    @Test
    void shouldPassBasicAssertion() {
        assertEquals(1 + 1, 2);
    }
}
