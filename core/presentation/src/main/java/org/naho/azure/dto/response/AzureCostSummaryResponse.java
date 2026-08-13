package org.naho.azure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AzureCostSummaryResponse {
    private BigDecimal cost;
    private String currency;
    private String period;
}
