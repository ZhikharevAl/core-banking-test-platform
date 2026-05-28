package org.example.banking.deposit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class DepositTests {

    @Test
    void newDepositIsOpenTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1000"));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(deposit.isClosed())
                    .as("closed flag")
                    .isFalse();
            softly.assertThat(deposit.currentBalance())
                    .as("balance")
                    .isEqualTo(new BigDecimal("1000"));
        });
    }

    @Test
    void topUpIncreasesBalanceTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1000"));

        deposit.topUp(new BigDecimal("200"));

        assertThat(deposit.currentBalance())
                .as("balance")
                .isEqualTo(new BigDecimal("1200"));
    }

    @Test
    void closePaysOutFullBalanceAndMarksClosedTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1500"));

        final BigDecimal payout = deposit.close();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(payout)
                    .as("payout")
                    .isEqualTo(new BigDecimal("1500"));
            softly.assertThat(deposit.isClosed())
                    .as("closed flag")
                    .isTrue();
        });
    }

    @Test
    void operationsOnClosedDepositFailTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("100"));
        deposit.close();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> deposit.topUp(BigDecimal.ONE))
                    .as("topUp on closed")
                    .isInstanceOf(IllegalStateException.class);
            softly.assertThatThrownBy(deposit::currentBalance)
                    .as("currentBalance on closed")
                    .isInstanceOf(IllegalStateException.class);
            softly.assertThatThrownBy(deposit::close)
                    .as("close on closed")
                    .isInstanceOf(IllegalStateException.class);
        });
    }

    @Test
    void closedFlagSurvivesAfterCloseTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, BigDecimal.ZERO);
        deposit.close();

        assertThat(deposit.isClosed())
                .as("closed flag")
                .isTrue();
    }

}
