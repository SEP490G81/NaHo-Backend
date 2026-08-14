package org.naho.speech.llm.dto.response;

import org.naho.speech.llm.result.OpenAiCostStatResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OpenAiCostStatResponse(
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

    public static OpenAiCostStatResponse fromResult(OpenAiCostStatResult result) {
        if (result == null) {
            return null;
        }

        Summary summary = new Summary(
                result.summary().totalCostUsd(),
                result.summary().currency()
        );

        List<DailyCost> dailyCosts = result.dailyCosts().stream()
                .map(dc -> new DailyCost(
                        dc.date(),
                        dc.costUsd()
                ))
                .toList();

        return new OpenAiCostStatResponse(
                result.yearMonth(),
                summary,
                dailyCosts
        );
    }
}
