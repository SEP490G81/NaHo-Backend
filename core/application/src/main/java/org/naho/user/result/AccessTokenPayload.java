package org.naho.user.result;

import java.time.Instant;
import java.util.List;

public record AccessTokenPayload(
        Long userId,
        Long userSessionId,
        List<String> roles,
        Instant expiresAt
) {

}
