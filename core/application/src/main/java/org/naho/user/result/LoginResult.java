package org.naho.user.result;

public record LoginResult(
        UserResult user,
        TokenResult token
) {
}
