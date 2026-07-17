package org.naho.league.command;

public record LeagueCommand(
        Long id,
        Long iconFileId,
        String name,
        String description,
        Double minPoint,
        Double maxPoint
) {
}
