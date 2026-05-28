package org.naho.user.dto.response;

import java.time.Instant;

public record TokenResponse(
        String value,
        Instant expiresAt
) {
}
