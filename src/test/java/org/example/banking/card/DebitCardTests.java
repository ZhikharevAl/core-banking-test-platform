package org.example.banking.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class DebitCardTests {

    @Test
    void topUpIncreasesBalanceTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.topUp(new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), card.currentBalance());
    }

    @Test
    void withdrawDecreasesBalanceTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.withdraw(new BigDecimal("40"));

        assertEquals(new BigDecimal("60"), card.currentBalance());
    }

    @Test
    void zeroAmountsAreAcceptedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.topUp(BigDecimal.ZERO);
        card.withdraw(BigDecimal.ZERO);

        assertEquals(new BigDecimal("100"), card.currentBalance());
    }

    @Test
    void negativeAmountsAreRejectedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        assertThrows(IllegalArgumentException.class, () -> card.topUp(new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> card.withdraw(new BigDecimal("-1")));
    }

    @Test
    void nullAmountsAreRejectedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        assertThrows(IllegalArgumentException.class, () -> card.topUp(null));
        assertThrows(IllegalArgumentException.class, () -> card.withdraw(null));
    }

    @Test
    void exposesNameAndCurrencyTest() {
        final DebitCard card = new DebitCard("Travel", CurrencyCode.EUR, new BigDecimal("100"));

        assertEquals("Travel", card.name());
        assertEquals(CurrencyCode.EUR, card.currency());
    }

}
