package org.naho.user.command;

public record LogoutCommand(
        Long userId,
        Long userSessionId
) {
}
