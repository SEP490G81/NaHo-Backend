package org.naho.user.result;

import java.time.Instant;

public record LoginResult(
        UserResult user,
        String accessToken,
        String refreshToken,
        Instant expireAt
) {
}
