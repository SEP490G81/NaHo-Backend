package org.naho.cost.aws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwsCostChartResponse {
    private BigDecimal totalCost;
    private String currency;
    private String granularity;
    private List<AwsCostPointResponse> points;
}
