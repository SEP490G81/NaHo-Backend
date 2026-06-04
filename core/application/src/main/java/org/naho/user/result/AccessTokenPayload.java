package org.naho.user.result;

import java.time.Instant;

public record AccessTokenPayload(
        Long userId,
        Long userSessionId,
        Instant expiresAt
) {

}
