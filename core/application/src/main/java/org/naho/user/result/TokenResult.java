package org.naho.user.result;

public record TokenResult(
        String accessToken,
        String refreshToken
) {
}
