package org.naho.user.event;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String fullName,
        String otpCode
) {
}