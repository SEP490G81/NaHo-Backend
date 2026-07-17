package org.naho.season.result;

import java.time.Instant;

public record SeasonResult(
        Long id,
        Integer seasonNo,
        Instant startAt,
        Instant endAt
) {
}
