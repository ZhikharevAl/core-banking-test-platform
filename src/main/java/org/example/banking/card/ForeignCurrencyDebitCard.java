package org.example.banking.card;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;

/**
 * Специализация дебетовой карты для валютных счетов.
 */
public final class ForeignCurrencyDebitCard extends AbstractCardProduct {

    public ForeignCurrencyDebitCard(final String name, final CurrencyCode currency, final BigDecimal openingBalance) {
        super(name, currency, openingBalance);
        if (currency == CurrencyCode.RUB) {
            throw new IllegalArgumentException("Foreign currency card cannot be in RUB");
        }
    }
}
