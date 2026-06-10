package org.naho.user.command;

public record CredentialsLoginCommand(
        String usernameOrEmail,
        String rawPassword,
        String deviceId,
        String userAgent,
        String ipAddress
) {
}
