package org.naho.cost.result;

import java.math.BigDecimal;

public class AwsCostSummaryResult {
    private final BigDecimal cost;
    private final String currency;
    private final String period;

    public AwsCostSummaryResult(BigDecimal cost, String currency, String period) {
        this.cost = cost;
        this.currency = currency;
        this.period = period;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPeriod() {
        return period;
    }
}
