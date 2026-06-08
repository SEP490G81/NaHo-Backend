package org.naho.user.dto.response;

import java.time.Instant;

public record TokenExpResponse(
        Instant expiresAt,
        Long expiresIn
) {
}
