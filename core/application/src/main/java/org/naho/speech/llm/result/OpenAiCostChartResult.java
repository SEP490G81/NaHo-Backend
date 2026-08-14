package org.naho.speech.llm.result;

import java.math.BigDecimal;
import java.util.List;

public class OpenAiCostChartResult {
    private final BigDecimal totalCost;
    private final String currency;
    private final String granularity;
    private final List<OpenAiCostPointResult> points;

    public OpenAiCostChartResult(BigDecimal totalCost, String currency, String granularity, List<OpenAiCostPointResult> points) {
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

    public List<OpenAiCostPointResult> getPoints() {
        return points;
    }
}
