package org.naho.chest.command;

public record OpenChestCommand(
        Long chestId,
        Long userId
) {
}
