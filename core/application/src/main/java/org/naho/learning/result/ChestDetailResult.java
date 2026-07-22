package org.naho.learning.result;

import org.naho.chest.type.ChestType;

public record ChestDetailResult(
        Long id,
        ChestType chestType,
        String description,
        Double minPoint,
        Double maxPoint
) {
}
