package org.naho.cost.result;

import java.math.BigDecimal;

public class AwsCostPointResult {
    private final String dateOrMonth;
    private final BigDecimal cost;
    private final String currency;

    public AwsCostPointResult(String dateOrMonth, BigDecimal cost, String currency) {
        this.dateOrMonth = dateOrMonth;
        this.cost = cost;
        this.currency = currency;
    }

    public String getDateOrMonth() {
        return dateOrMonth;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }
}
