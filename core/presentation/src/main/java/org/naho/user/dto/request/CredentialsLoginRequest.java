package org.naho.user.dto.request;

public record CredentialsLoginRequest(
        String username,
        String rawPassword
) {
}
