package org.example.banking.card;

import java.math.BigDecimal;
import org.example.banking.common.BankingProduct;

public interface CardProduct extends BankingProduct {
    void topUp(BigDecimal amount);

    void withdraw(BigDecimal amount);

    BigDecimal currentBalance();
}
