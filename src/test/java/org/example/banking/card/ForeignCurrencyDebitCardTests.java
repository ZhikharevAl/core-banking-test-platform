package org.example.banking.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ForeignCurrencyDebitCardTests {

    @ParameterizedTest
    @EnumSource(value = CurrencyCode.class, names = {"USD", "EUR"})
    void canBeOpenedInForeignCurrencyTest(final CurrencyCode currency) {
        final ForeignCurrencyDebitCard card = new ForeignCurrencyDebitCard("Travel", currency, new BigDecimal("100"));

        assertEquals(currency, card.currency());
        assertEquals(new BigDecimal("100"), card.currentBalance());
    }

    @Test
    void rejectsRubCurrencyTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ForeignCurrencyDebitCard("Bad", CurrencyCode.RUB, BigDecimal.TEN)
        );
    }

    @Test
    void supportsTopUpAndWithdrawTest() {
        final ForeignCurrencyDebitCard card = new ForeignCurrencyDebitCard("Travel", CurrencyCode.USD, BigDecimal.ZERO);

        card.topUp(new BigDecimal("200"));
        card.withdraw(new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), card.currentBalance());
    }

}
