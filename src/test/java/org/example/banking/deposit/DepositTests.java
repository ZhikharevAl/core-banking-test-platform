package org.example.banking.deposit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;
import org.junit.jupiter.api.Test;

class DepositTests {

    @Test
    void newDepositIsOpenTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1000"));

        assertFalse(deposit.isClosed());
        assertEquals(new BigDecimal("1000"), deposit.currentBalance());
    }

    @Test
    void topUpIncreasesBalanceTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1000"));

        deposit.topUp(new BigDecimal("200"));

        assertEquals(new BigDecimal("1200"), deposit.currentBalance());
    }

    @Test
    void closePaysOutFullBalanceAndMarksClosedTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("1500"));

        final BigDecimal payout = deposit.close();

        assertEquals(new BigDecimal("1500"), payout);
        assertTrue(deposit.isClosed());
    }

    @Test
    void operationsOnClosedDepositFailTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("100"));
        deposit.close();

        assertThrows(IllegalStateException.class, () -> deposit.topUp(BigDecimal.ONE));
        assertThrows(IllegalStateException.class, deposit::currentBalance);
        assertThrows(IllegalStateException.class, deposit::close);
    }

    @Test
    void closedFlagSurvivesAfterCloseTest() {
        final Deposit deposit = new Deposit("Savings", CurrencyCode.EUR, BigDecimal.ZERO);
        deposit.close();

        assertTrue(deposit.isClosed());
    }

}
