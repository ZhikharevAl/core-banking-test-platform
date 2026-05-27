package org.example.banking.common;

import java.math.BigDecimal;

/**
 * Базовая реализация общей части банковских продуктов.
 */
public abstract class AbstractBankingProduct implements BankingProduct {
    private final String name;
    private final CurrencyCode currency;
    private BigDecimal balance;

    protected AbstractBankingProduct(final String name, final CurrencyCode currency, final BigDecimal openingBalance) {
        validateAmount(openingBalance, "openingBalance");
        this.name = requireNotBlank(name, "name");
        this.currency = requireNonNull(currency, "currency");
        this.balance = openingBalance;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final CurrencyCode currency() {
        return currency;
    }

    @Override
    public final BigDecimal balance() {
        return balance;
    }

    protected final void increaseBalance(final BigDecimal amount) {
        validateAmount(amount, "amount");
        balance = balance.add(amount);
    }

    protected final void decreaseBalance(final BigDecimal amount) {
        validateAmount(amount, "amount");
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }

    protected static void validateAmount(final BigDecimal amount, final String field) {
        requireNonNull(amount, field);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(field + " must be >= 0");
        }
    }

    protected static String requireNotBlank(final String value, final String field) {
        requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    protected static <T> T requireNonNull(final T value, final String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        return value;
    }

}
