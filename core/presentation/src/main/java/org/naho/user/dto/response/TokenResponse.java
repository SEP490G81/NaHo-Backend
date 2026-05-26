package org.naho.user.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
