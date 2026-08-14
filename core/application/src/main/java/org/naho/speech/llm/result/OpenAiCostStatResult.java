package org.naho.speech.llm.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OpenAiCostStatResult(
        String yearMonth,
        Summary summary,
        List<DailyCost> dailyCosts
) {
    public record Summary(
            BigDecimal totalCostUsd,
            String currency
    ) {}

    public record DailyCost(
            LocalDate date,
            BigDecimal costUsd
    ) {}
}
