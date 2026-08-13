package org.naho.user.command;

public record RegisterCommand(
        String username,
        String password,
        String email,
        String fullname
) {
}
