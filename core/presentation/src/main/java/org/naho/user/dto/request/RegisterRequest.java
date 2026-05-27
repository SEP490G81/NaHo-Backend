package org.naho.user.dto.request;

public record RegisterRequest(
        String username,
        String password,
        String email
) {
}
