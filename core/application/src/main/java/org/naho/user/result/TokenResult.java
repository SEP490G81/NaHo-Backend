package org.naho.user.result;

import java.time.Instant;

public record TokenResult(
        String cookieName,
        String tokenName,
        String value,
        Instant expiresAt,
        Long expiresIn
) {
}
