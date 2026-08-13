package org.naho.azure.dto.request;

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
public class AzureCostChartRequest {
    private String timeframe;
    private String granularity;
    private String fromDate;
    private String toDate;
}
