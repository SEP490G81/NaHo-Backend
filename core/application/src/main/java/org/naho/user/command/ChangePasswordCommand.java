package org.naho.user.command;

public record ChangePasswordCommand(
        Long userId,
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}
