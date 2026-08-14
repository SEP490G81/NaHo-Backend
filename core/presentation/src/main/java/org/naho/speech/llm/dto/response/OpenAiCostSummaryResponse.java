package org.naho.speech.llm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCostSummaryResponse {
    private BigDecimal cost;
    private String currency;
    private String period;
}
