package org.naho.user.result;

public record LoginResult(
        UserResult user,
        TokenResult accessToken,
        TokenResult refreshToken
) {
}
