package org.naho.chest.command;

public record OpenChestCommand(
        Long learningPathNodeId,
        Long userId
) {
}
