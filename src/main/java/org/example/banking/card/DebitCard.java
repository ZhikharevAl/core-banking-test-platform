package org.example.banking.card;

import java.math.BigDecimal;
import org.example.banking.common.CurrencyCode;

public final class DebitCard extends AbstractCardProduct {

    public DebitCard(final String name, final CurrencyCode currency, final BigDecimal openingBalance) {
        super(name, currency, openingBalance);
    }

}
