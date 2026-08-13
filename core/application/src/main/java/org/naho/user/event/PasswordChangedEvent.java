package org.naho.user.event;

public record PasswordChangedEvent(
        Long userId,
        String email,
        String fullName
) {
}
