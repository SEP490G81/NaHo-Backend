package org.naho.season.command;

import java.time.Instant;

public record CreateSeasonCommand(
        Instant startAt,
        Instant endAt
) {
}

