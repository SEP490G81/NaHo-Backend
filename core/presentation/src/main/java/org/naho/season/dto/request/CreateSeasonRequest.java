package org.naho.season.dto.request;
import jakarta.validation.constraints.NotNull;
import org.naho.i18n.message.season.SeasonDetailMessageKey;

import java.time.Instant;

public record CreateSeasonRequest(
        @NotNull(message = SeasonDetailMessageKey.SEASON_START_AT_EMPTY)
        Instant startAt,
        @NotNull(message = SeasonDetailMessageKey.SEASON_END_AT_EMPTY)
        Instant endAt
) {
}
