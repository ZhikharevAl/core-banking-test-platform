package org.example.banking.deposit;

import java.math.BigDecimal;
import org.example.banking.common.AbstractBankingProduct;
import org.example.banking.common.CurrencyCode;

public final class Deposit extends AbstractBankingProduct {
    private boolean closed;

    public Deposit(final String name, final CurrencyCode currency, final BigDecimal openingBalance) {
        super(name, currency, openingBalance);
        this.closed = false;
    }

    public void topUp(final BigDecimal amount) {
        ensureNotClosed();
        increaseBalance(amount);
    }

    public BigDecimal currentBalance() {
        ensureNotClosed();
        return balance();
    }

    public BigDecimal close() {
        ensureNotClosed();
        final BigDecimal payout = balance();
        decreaseBalance(payout);
        closed = true;
        return payout;
    }

    public boolean isClosed() {
        return closed;
    }

    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Deposit is closed");
        }
    }
}
