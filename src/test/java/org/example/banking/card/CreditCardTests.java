package org.example.banking.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class CreditCardTests {

    private static CreditCard newCard(final String balance) {
        return new CreditCard("Credit", CurrencyCode.RUB, new BigDecimal(balance), new BigDecimal("0.25"));
    }

    @Test
    void exposesInterestRateTest() {
        final CreditCard card = newCard("0");

        assertThat(card.interestRate())
                .as("interest rate")
                .isEqualTo(new BigDecimal("0.25"));
    }

    @Test
    void withdrawWithinBalanceDoesNotCreateDebtTest() {
        final CreditCard card = newCard("500");

        card.withdraw(new BigDecimal("200"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(new BigDecimal("300"));
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(BigDecimal.ZERO);
        });
    }

    @Test
    void withdrawAboveBalanceCreatesDebtTest() {
        final CreditCard card = newCard("100");

        card.withdraw(new BigDecimal("250"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(BigDecimal.ZERO);
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(new BigDecimal("150"));
        });
    }

    @Test
    void withdrawFromZeroBalanceCreatesFullDebtTest() {
        final CreditCard card = newCard("0");

        card.withdraw(new BigDecimal("400"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(BigDecimal.ZERO);
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(new BigDecimal("400"));
        });
    }

    @Test
    void topUpRepaysDebtFirstTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("100"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(BigDecimal.ZERO);
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(new BigDecimal("200"));
        });
    }

    @Test
    void topUpExactlyClosesDebtTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("300"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(BigDecimal.ZERO);
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(BigDecimal.ZERO);
        });
    }

    @Test
    void topUpExceedingDebtPutsRemainderOnBalanceTest() {
        final CreditCard card = newCard("0");
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("500"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(new BigDecimal("200"));
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(BigDecimal.ZERO);
        });
    }

    @Test
    void topUpWithoutDebtJustIncreasesBalanceTest() {
        final CreditCard card = newCard("100");

        card.topUp(new BigDecimal("50"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(card.currentBalance())
                    .as("balance")
                    .isEqualTo(new BigDecimal("150"));
            softly.assertThat(card.currentDebt())
                    .as("debt")
                    .isEqualTo(BigDecimal.ZERO);
        });
    }

    @Test
    void negativeInterestRateIsRejectedTest() {
        assertThatThrownBy(() -> new CreditCard("Bad", CurrencyCode.RUB, BigDecimal.ZERO, new BigDecimal("-0.1")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullInterestRateIsRejectedTest() {
        assertThatThrownBy(() -> new CreditCard("Bad", CurrencyCode.RUB, BigDecimal.ZERO, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
