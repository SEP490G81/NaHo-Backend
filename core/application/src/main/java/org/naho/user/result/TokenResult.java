package org.naho.user.result;

import java.time.Instant;

public record TokenResult(
        String value,
        Instant expiresAt
) {
}
