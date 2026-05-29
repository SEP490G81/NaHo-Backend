package org.naho.user.result;

public record RegisterResult(
        String id,
        String username,
        String email
) {
}
