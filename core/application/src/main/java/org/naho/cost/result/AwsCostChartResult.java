package org.naho.cost.result;

import java.math.BigDecimal;
import java.util.List;

public class AwsCostChartResult {
    private final BigDecimal totalCost;
    private final String currency;
    private final String granularity;
    private final List<AwsCostPointResult> points;

    public AwsCostChartResult(BigDecimal totalCost, String currency, String granularity,
                              List<AwsCostPointResult> points) {
        this.totalCost = totalCost;
        this.currency = currency;
        this.granularity = granularity;
        this.points = points;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getCurrency() {
        return currency;
    }

    public String getGranularity() {
        return granularity;
    }

    public List<AwsCostPointResult> getPoints() {
        return points;
    }
}
