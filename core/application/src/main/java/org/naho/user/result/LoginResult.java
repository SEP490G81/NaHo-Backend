package org.naho.user.result;

public record LoginResult(
        TokenResult accessToken,
        TokenResult refreshToken
) {
}
