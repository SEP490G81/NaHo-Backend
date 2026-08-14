package org.naho.cost.llm.dto.request;

import lombok.*;

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
