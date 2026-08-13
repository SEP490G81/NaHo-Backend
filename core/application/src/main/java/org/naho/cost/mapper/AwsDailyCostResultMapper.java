package org.naho.cost.mapper;

import org.naho.cost.model.AwsDailyCost;
import org.naho.cost.result.AwsDailyCostResult;

public class AwsDailyCostResultMapper {
    public AwsDailyCostResult domainToResult(AwsDailyCost domain) {
        return AwsDailyCostResult.builder()
                .id(domain.getId())
                .recordDate(domain.getRecordDate())
                .costAmount(domain.getCostAmount())
                .currency(domain.getCurrency())
                .build();
    }
}
