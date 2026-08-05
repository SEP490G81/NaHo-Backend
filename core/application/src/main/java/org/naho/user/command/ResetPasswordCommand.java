package org.naho.user.command;

public record ResetPasswordCommand(
        String email,
        String resetToken,
        String newPassword,
        String confirmPassword
) {
}
