package org.example.banking.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.example.banking.card.DebitCard;
import org.junit.jupiter.api.Test;

class AbstractBankingProductValidationTests {

    @Test
    void nullNameIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard(null, CurrencyCode.RUB, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be null");
    }

    @Test
    void blankNameIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard("   ", CurrencyCode.RUB, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void emptyNameIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard("", CurrencyCode.RUB, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullCurrencyIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard("Card", null, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("currency must not be null");
    }

    @Test
    void nullOpeningBalanceIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard("Card", CurrencyCode.RUB, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("openingBalance must not be null");
    }

    @Test
    void negativeOpeningBalanceIsRejectedTest() {
        assertThatThrownBy(() -> new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("openingBalance must be >= 0");
    }

    @Test
    void zeroOpeningBalanceIsAcceptedTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, BigDecimal.ZERO);

        assertThat(card.currentBalance())
                .as("balance")
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void insufficientFundsOnWithdrawTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("10"));

        assertThatThrownBy(() -> card.withdraw(new BigDecimal("11")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds");
    }

    @Test
    void exactBalanceWithdrawSucceedsTest() {
        final DebitCard card = new DebitCard("Card", CurrencyCode.RUB, new BigDecimal("10"));

        card.withdraw(new BigDecimal("10"));

        assertThat(card.currentBalance())
                .as("balance")
                .isEqualTo(BigDecimal.ZERO);
    }

}
