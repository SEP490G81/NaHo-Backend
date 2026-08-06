package org.naho.user.command;

public record VerifyForgotPasswordOtpCommand(
        String email,
        String otpCode
) {
}
