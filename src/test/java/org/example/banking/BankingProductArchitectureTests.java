package org.example.banking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.example.banking.card.CardProduct;
import org.example.banking.card.CreditCard;
import org.example.banking.card.DebitCard;
import org.example.banking.card.ForeignCurrencyDebitCard;
import org.example.banking.common.BankingProduct;
import org.example.banking.common.CurrencyCode;
import org.example.banking.deposit.Deposit;
import org.junit.jupiter.api.Test;

class BankingProductArchitectureTests {

    @Test
    void allProductsShareCommonContractTest() {
        final List<BankingProduct> products = List.of(
                new DebitCard("Daily card", CurrencyCode.RUB, new BigDecimal("1000")),
                new ForeignCurrencyDebitCard("Travel card", CurrencyCode.USD, new BigDecimal("50")),
                new CreditCard("Credit line", CurrencyCode.RUB, new BigDecimal("200"), new BigDecimal("0.25")),
                new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("5000"))
        );

        assertEquals(4, products.size());
        assertTrue(products.stream().allMatch(p -> p.balance().signum() >= 0));
    }

    @Test
    void debitCardSupportsTopUpWithdrawAndBalanceTest() {
        final DebitCard card = new DebitCard("Main debit", CurrencyCode.RUB, new BigDecimal("100"));

        card.topUp(new BigDecimal("50"));
        card.withdraw(new BigDecimal("60"));

        assertEquals(new BigDecimal("90"), card.currentBalance());
    }

    @Test
    void foreignCurrencyCardMustUseNonRubCurrencyTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ForeignCurrencyDebitCard("Bad card", CurrencyCode.RUB, BigDecimal.TEN)
        );
    }

    @Test
    void creditCardCalculatesDebtWhenWithdrawalExceedsBalanceTest() {
        final CreditCard card = new CreditCard(
                "Credit",
                CurrencyCode.RUB,
                new BigDecimal("100"),
                new BigDecimal("0.3")
        );

        card.withdraw(new BigDecimal("250"));

        assertEquals(BigDecimal.ZERO, card.currentBalance());
        assertEquals(new BigDecimal("150"), card.currentDebt());
        assertEquals(new BigDecimal("0.3"), card.interestRate());
    }

    @Test
    void creditCardTopUpRepaysDebtFirstTest() {
        final CreditCard card = new CreditCard("Credit", CurrencyCode.USD, BigDecimal.ZERO, new BigDecimal("0.2"));
        card.withdraw(new BigDecimal("300"));

        card.topUp(new BigDecimal("350"));

        assertEquals(BigDecimal.ZERO, card.currentDebt());
        assertEquals(new BigDecimal("50"), card.currentBalance());
    }

    @Test
    void depositSupportsTopUpBalanceAndCloseTest() {
        final Deposit deposit = new Deposit("Long-term", CurrencyCode.EUR, new BigDecimal("1000"));
        deposit.topUp(new BigDecimal("200"));

        assertEquals(new BigDecimal("1200"), deposit.currentBalance());
        assertEquals(new BigDecimal("1200"), deposit.close());
        assertTrue(deposit.isClosed());
        assertThrows(IllegalStateException.class, () -> deposit.topUp(BigDecimal.ONE));
    }

    @Test
    void architectureSupportsTypeBasedExtensionsWithoutBreakingBaseContractTest() {
        final BankingProduct product = new CreditCard("Typed", CurrencyCode.RUB, BigDecimal.TEN, new BigDecimal("0.1"));

        assertInstanceOf(CardProduct.class, product);
        assertInstanceOf(CreditCard.class, product);
    }

}
