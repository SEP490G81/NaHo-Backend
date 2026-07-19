package org.naho.chest.dto.request;

public record OpenChestRequest(
        Long chestId,
        Long userId
) {
}
