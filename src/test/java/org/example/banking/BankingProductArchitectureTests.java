package org.example.banking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import org.example.banking.deposit.DepositProduct;
import org.junit.jupiter.api.Test;

class BankingProductArchitectureTests {

    @Test
    void allProductsShareCommonContractTest() {
        final List<BankingProduct> products = List.of(
                new DebitCard("Daily", CurrencyCode.RUB, new BigDecimal("1000")),
                new ForeignCurrencyDebitCard("Travel", CurrencyCode.USD, new BigDecimal("50")),
                new CreditCard("Credit", CurrencyCode.RUB, new BigDecimal("200"), new BigDecimal("0.25")),
                new Deposit("Savings", CurrencyCode.EUR, new BigDecimal("5000"))
        );

        assertEquals(4, products.size());
        assertTrue(products.stream().allMatch(p -> p.name() != null && !p.name().isBlank()));
        assertTrue(products.stream().allMatch(p -> p.currency() != null));
        assertTrue(products.stream().allMatch(p -> p.balance().signum() >= 0));
    }

    @Test
    void allCardsExposeCardOperationsTest() {
        final List<CardProduct> cards = List.of(
                new DebitCard("D", CurrencyCode.RUB, BigDecimal.TEN),
                new ForeignCurrencyDebitCard("F", CurrencyCode.USD, BigDecimal.TEN),
                new CreditCard("C", CurrencyCode.RUB, BigDecimal.TEN, new BigDecimal("0.1"))
        );

        for (final CardProduct card : cards) {
            card.topUp(BigDecimal.ONE);
            card.withdraw(BigDecimal.ONE);
            assertEquals(BigDecimal.TEN, card.currentBalance());
        }
    }

    @Test
    void creditCardIsRecognizableAsCardAndProductTest() {
        final BankingProduct product = new CreditCard("Typed", CurrencyCode.RUB, BigDecimal.TEN, new BigDecimal("0.1"));

        assertInstanceOf(CardProduct.class, product);
        assertInstanceOf(CreditCard.class, product);
    }

    @Test
    void depositIsRecognizableAsDepositProductAndBankingProductTest() {
        final BankingProduct product = new Deposit("Savings", CurrencyCode.EUR, BigDecimal.TEN);

        assertInstanceOf(DepositProduct.class, product);
        assertInstanceOf(Deposit.class, product);
    }

}
