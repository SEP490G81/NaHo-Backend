package org.naho.user.dto.response;

public record LoginResponse(
        UserResponse user,
        TokenResponse accessToken
) {
}
