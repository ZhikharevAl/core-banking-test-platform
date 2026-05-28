package org.example.banking;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
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

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(products)
                    .as("products count")
                    .hasSize(4);
            softly.assertThat(products)
                    .as("each product exposes a non-blank name")
                    .allMatch(p -> p.name() != null && !p.name().isBlank());
            softly.assertThat(products)
                    .as("each product exposes a currency")
                    .allMatch(p -> p.currency() != null);
            softly.assertThat(products)
                    .as("each product has a non-negative balance")
                    .allMatch(p -> p.balance().signum() >= 0);
        });
    }

    @Test
    void allCardsExposeCardOperationsTest() {
        final List<CardProduct> cards = List.of(
                new DebitCard("D", CurrencyCode.RUB, BigDecimal.TEN),
                new ForeignCurrencyDebitCard("F", CurrencyCode.USD, BigDecimal.TEN),
                new CreditCard("C", CurrencyCode.RUB, BigDecimal.TEN, new BigDecimal("0.1"))
        );

        SoftAssertions.assertSoftly(softly -> {
            for (final CardProduct card : cards) {
                card.topUp(BigDecimal.ONE);
                card.withdraw(BigDecimal.ONE);
                softly.assertThat(card.currentBalance())
                        .as("balance of %s after topUp+withdraw", card.name())
                        .isEqualTo(BigDecimal.TEN);
            }
        });
    }

    @Test
    void creditCardIsRecognizableAsCardAndProductTest() {
        final BankingProduct product = new CreditCard("Typed", CurrencyCode.RUB, BigDecimal.TEN, new BigDecimal("0.1"));

        assertThat(product)
                .isInstanceOf(CardProduct.class)
                .isInstanceOf(CreditCard.class);
    }

    @Test
    void depositIsRecognizableAsDepositProductAndBankingProductTest() {
        final BankingProduct product = new Deposit("Savings", CurrencyCode.EUR, BigDecimal.TEN);

        assertThat(product)
                .isInstanceOf(DepositProduct.class)
                .isInstanceOf(Deposit.class);
    }

}
