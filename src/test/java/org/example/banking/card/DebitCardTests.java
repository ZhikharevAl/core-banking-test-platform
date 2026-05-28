package org.example.banking.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class DebitCardTests {

    @Test
    void topUpIncreasesBalanceTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.topUp(new BigDecimal("50"));

        assertThat(card.currentBalance())
                .as("balance")
                .isEqualTo(new BigDecimal("150"));
    }

    @Test
    void withdrawDecreasesBalanceTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.withdraw(new BigDecimal("40"));

        assertThat(card.currentBalance())
                .as("balance")
                .isEqualTo(new BigDecimal("60"));
    }

    @Test
    void zeroAmountsAreAcceptedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        card.topUp(BigDecimal.ZERO);
        card.withdraw(BigDecimal.ZERO);

        assertThat(card.currentBalance())
                .as("balance unchanged after zero ops")
                .isEqualTo(new BigDecimal("100"));
    }

    @Test
    void negativeAmountsAreRejectedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> card.topUp(new BigDecimal("-1")))
                    .as("topUp negative")
                    .isInstanceOf(IllegalArgumentException.class);
            softly.assertThatThrownBy(() -> card.withdraw(new BigDecimal("-1")))
                    .as("withdraw negative")
                    .isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void nullAmountsAreRejectedTest() {
        final DebitCard card = new DebitCard("Main", CurrencyCode.RUB, new BigDecimal("100"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> card.topUp(null))
                    .as("topUp null")
                    .isInstanceOf(IllegalArgumentException.class);
            softly.assertThatThrownBy(() -> card.withdraw(null))
                    .as("withdraw null")
                    .isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void exposesNameAndCurrencyTest() {
        final DebitCard card = new DebitCard("Travel", CurrencyCode.EUR, new BigDecimal("100"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.name())
                    .as("name")
                    .isEqualTo("Travel");
            softly.assertThat(card.currency())
                    .as("currency")
                    .isEqualTo(CurrencyCode.EUR);
        });
    }

}
