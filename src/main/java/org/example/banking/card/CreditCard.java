package org.example.banking.card;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;

public final class CreditCard extends AbstractCardProduct {
    private final BigDecimal interestRate;
    private BigDecimal debt;

    public CreditCard(
            final String name,
            final CurrencyCode currency,
            final BigDecimal openingBalance,
            final BigDecimal interestRate
    ) {
        super(name, currency, openingBalance);
        validateAmount(interestRate, "interestRate");
        this.interestRate = interestRate;
        this.debt = BigDecimal.ZERO;
    }

    @Override
    public void withdraw(final BigDecimal amount) {
        validateAmount(amount, "amount");
        if (balance().compareTo(amount) >= 0) {
            decreaseBalance(amount);
            return;
        }
        final BigDecimal unavailablePart = amount.subtract(balance());
        if (balance().signum() > 0) {
            decreaseBalance(balance());
        }
        debt = debt.add(unavailablePart);
    }

    @Override
    public void topUp(final BigDecimal amount) {
        validateAmount(amount, "amount");
        if (debt.signum() > 0) {
            final BigDecimal paymentToDebt = amount.min(debt);
            debt = debt.subtract(paymentToDebt);
            final BigDecimal remainder = amount.subtract(paymentToDebt);
            if (remainder.signum() > 0) {
                increaseBalance(remainder);
            }
            return;
        }
        increaseBalance(amount);
    }

    public BigDecimal currentDebt() {
        return debt;
    }

    public BigDecimal interestRate() {
        return interestRate;
    }
}
