package org.example.banking.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class CreditCardTests {

    private static CreditCard newCard(final String balance) {
        return new CreditCard("Credit", CurrencyCode.RUB, new BigDecimal(balance), new BigDecimal("0.25"));
    }

    @Test
    void exposesInterestRateTest() {
        final CreditCard card = newCard("0");

        assertEquals(new BigDecimal("0.25"), card.interestRate());
    }

    @Test
    void withdrawWithinBalanceDoesNotCreateDebtTest() {
        final CreditCard card = newCard("500");

        card.withdraw(new BigDecimal("200"));

        assertEquals(new BigDecimal("300"), card.currentBalance());
        assertEquals(BigDecimal.ZERO, card.currentDebt());
    }

    @Test
    void withdrawAboveBalanceCreatesDebtTest() {
        final CreditCard card = newCard("100");

        card.withdraw(new BigDecimal("250"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
        assertEquals(new BigDecimal("150"), card.currentDebt());
    }

    @Test
    void withdrawFromZeroBalanceCreatesFullDebtTest() {
        final CreditCard card = newCard("0");

        card.withdraw(new BigDecimal("400"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
        assertEquals(new BigDecimal("400"), card.currentDebt());
    }

    @Test
    void topUpRepaysDebtFirstTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("100"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
        assertEquals(new BigDecimal("200"), card.currentDebt());
    }

    @Test
    void topUpExactlyClosesDebtTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("300"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
        assertEquals(BigDecimal.ZERO, card.currentDebt());
    }

    @Test
    void topUpExceedingDebtPutsRemainderOnBalanceTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("500"));

        assertEquals(new BigDecimal("200"), card.currentBalance());
        assertEquals(BigDecimal.ZERO, card.currentDebt());
    }

    @Test
    void topUpWithoutDebtJustIncreasesBalanceTest() {
        final CreditCard card = newCard("100");

        card.topUp(new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), card.currentBalance());
        assertEquals(BigDecimal.ZERO, card.currentDebt());
    }

    @Test
    void negativeInterestRateIsRejectedTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CreditCard("Bad", CurrencyCode.RUB, BigDecimal.ZERO, new BigDecimal("-0.1"))
        );
    }

    @Test
    void nullInterestRateIsRejectedTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CreditCard("Bad", CurrencyCode.RUB, BigDecimal.ZERO, null)
        );
    }

}
