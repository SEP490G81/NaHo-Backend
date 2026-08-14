package org.naho.cost.aws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwsCostPointResponse {
    private String dateOrMonth;
    private BigDecimal cost;
    private String currency;
}
