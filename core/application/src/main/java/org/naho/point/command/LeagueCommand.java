package org.naho.point.command;

public record LeagueCommand(
        Long id,
        Long iconFileId,
        String name,
        String description,
        Double minPoint,
        Double maxPoint
) {
}
