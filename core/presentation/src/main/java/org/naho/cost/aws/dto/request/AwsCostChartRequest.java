package org.naho.cost.aws.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwsCostChartRequest {
    private String timeframe;
    private String granularity;
    private String fromDate;
    private String toDate;
}
