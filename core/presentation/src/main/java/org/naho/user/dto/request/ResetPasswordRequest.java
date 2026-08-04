package org.naho.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mã xác thực không được để trống")
        String resetToken,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        String newPassword,

        @NotBlank(message = "Xác nhận mật khẩu không được để trống")
        String confirmPassword
) {
}
