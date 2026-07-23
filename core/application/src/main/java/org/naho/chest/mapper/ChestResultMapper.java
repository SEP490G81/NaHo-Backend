package org.naho.chest.mapper;

import org.naho.chest.model.Chest;
import org.naho.chest.result.ChestResult;

public class ChestResultMapper {
    public ChestResult domainToResult(Chest domain) {
        return ChestResult.builder()
                .id(domain.getId())
                .chestType(domain.getChestType())
                .description(domain.getDescription())
                .minPoint(domain.getMinPoint())
                .maxPoint(domain.getMaxPoint())
                .build();
    }
}
