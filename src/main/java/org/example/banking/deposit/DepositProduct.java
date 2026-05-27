package org.example.banking.deposit;

import java.math.BigDecimal;
import org.example.banking.common.BankingProduct;

/**
 * Контракт для депозитных продуктов: пополнение, запрос баланса и закрытие.
 * Закрытие возвращает выплачиваемую сумму и переводит продукт в терминальное состояние.
 */
public interface DepositProduct extends BankingProduct {
    void topUp(BigDecimal amount);

    BigDecimal currentBalance();

    BigDecimal close();

    boolean isClosed();

}
