package org.naho.user.command;

public record ResetPasswordCommand(
        String email,
        String otpCode,
        String newPassword,
        String confirmPassword
) {
}
