package org.example.banking.card;

import java.math.BigDecimal;
import org.example.banking.common.AbstractBankingProduct;
import org.example.banking.common.CurrencyCode;

/**
 * Шаблон для карточных продуктов.
 */
public abstract class AbstractCardProduct extends AbstractBankingProduct implements CardProduct {

    protected AbstractCardProduct(final String name, final CurrencyCode currency, final BigDecimal openingBalance) {
        super(name, currency, openingBalance);
    }

    @Override
    public void topUp(final BigDecimal amount) {
        increaseBalance(amount);
    }

    @Override
    public void withdraw(final BigDecimal amount) {
        decreaseBalance(amount);
    }

    @Override
    public BigDecimal currentBalance() {
        return balance();
    }

}
