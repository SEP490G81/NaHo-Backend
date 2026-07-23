package org.naho.chest.dto.response;

import org.naho.chest.type.ChestType;

public record ChestResponse(
        Long id,
        ChestType chestType,
        String description,
        Integer minPoint,
        Integer maxPoint
) {
}
