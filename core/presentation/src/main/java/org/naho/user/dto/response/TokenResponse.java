package org.naho.user.dto.response;

public record TokenResponse(
        String value,
        Long expireIn
) {
}
