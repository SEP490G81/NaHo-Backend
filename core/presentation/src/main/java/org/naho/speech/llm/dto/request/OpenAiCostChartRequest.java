package org.naho.speech.llm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCostChartRequest {
    private String timeframe;
    private String granularity;
    private String fromDate;
    private String toDate;
}
