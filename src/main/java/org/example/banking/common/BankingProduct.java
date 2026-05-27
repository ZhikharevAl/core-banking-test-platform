package org.example.banking.common;

import java.math.BigDecimal;

/**
 * Базовый контракт для любого банковского продукта.
 */
public interface BankingProduct {
    String name();

    CurrencyCode currency();

    BigDecimal balance();

}
