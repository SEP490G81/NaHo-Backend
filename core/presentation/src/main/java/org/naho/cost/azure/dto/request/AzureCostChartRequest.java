package org.naho.cost.azure.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzureCostChartRequest {
    private String timeframe;
    private String granularity;
    private String fromDate;
    private String toDate;
}
