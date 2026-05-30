package org.naho.user.dto.response;

public record RegisterResponse(
        String id,
        String username,
        String email
) {
}
