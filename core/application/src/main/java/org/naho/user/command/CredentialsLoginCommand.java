package org.naho.user.command;

public record CredentialsLoginCommand(
        String username,
        String rawPassword
) {
}
