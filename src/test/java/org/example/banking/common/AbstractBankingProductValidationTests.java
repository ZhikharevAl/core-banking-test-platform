package org.example.banking.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.example.banking.card.DebitCard;
import org.junit.jupiter.api.Test;

class AbstractBankingProductValidationTests {

    @Test
    void nullNameIsRejectedTest() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard(null, CurrencyCode.RUB, BigDecimal.ZERO)
        );
        assertEquals("name must not be null", ex.getMessage());
    }

    @Test
    void blankNameIsRejectedTest() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard("   ", CurrencyCode.RUB, BigDecimal.ZERO)
        );
        assertEquals("name must not be blank", ex.getMessage());
    }

    @Test
    void emptyNameIsRejectedTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard("", CurrencyCode.RUB, BigDecimal.ZERO)
        );
    }

    @Test
    void nullCurrencyIsRejectedTest() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard("Card", null, BigDecimal.ZERO)
        );
        assertEquals("currency must not be null", ex.getMessage());
    }

    @Test
    void nullOpeningBalanceIsRejectedTest() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard("Card", CurrencyCode.RUB, null)
        );
        assertEquals("openingBalance must not be null", ex.getMessage());
    }

    @Test
    void negativeOpeningBalanceIsRejectedTest() {
        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("-1"))
        );
        assertEquals("openingBalance must be >= 0", ex.getMessage());
    }

    @Test
    void zeroOpeningBalanceIsAcceptedTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, card.currentBalance());
    }

    @Test
    void insufficientFundsOnWithdrawTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("10"));

        final IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> card.withdraw(new BigDecimal("11"))
        );
        assertEquals("Insufficient funds", ex.getMessage());
    }

    @Test
    void exactBalanceWithdrawSucceedsTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("10"));

        card.withdraw(new BigDecimal("10"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
    }

}
