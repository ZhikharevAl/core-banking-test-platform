package org.example.banking.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ForeignCurrencyDebitCardTests {

    @ParameterizedTest
    @EnumSource(value = CurrencyCode.class, names = {"USD", "EUR"})
    void canBeOpenedInForeignCurrencyTest(final CurrencyCode currency) {
        final ForeignCurrencyDebitCard card = new ForeignCurrencyDebitCard("Travel", currency, new BigDecimal("100"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currency())
                    .as("currency")
                    .isEqualTo(currency);
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(new BigDecimal("100"));
        });
    }

    @Test
    void rejectsRubCurrencyTest() {
        assertThatThrownBy(() -> new ForeignCurrencyDebitCard("Bad", CurrencyCode.RUB, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void supportsTopUpAndWithdrawTest() {
        final ForeignCurrencyDebitCard card = new ForeignCurrencyDebitCard("Travel", CurrencyCode.USD, BigDecimal.ZERO);

        card.topUp(new BigDecimal("200"));
        card.withdraw(new BigDecimal("50"));

        assertThat(card.currentBalance())
                .as("balance")
                .isEqualTo(new BigDecimal("150"));
    }

}
