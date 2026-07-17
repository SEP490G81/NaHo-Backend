package org.naho.season.dto.response;

import java.time.Instant;

public record SeasonResponse(
        Long id,
        Integer seasonNo,
        Instant startAt,
        Instant endAt
) {
}
