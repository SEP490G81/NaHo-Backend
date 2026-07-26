package org.naho.user.command;

public record VerifyEmailCommand(
        String email,
        String otpCode,
        String deviceId,
        String userAgent,
        String ipAddress
) {
}
