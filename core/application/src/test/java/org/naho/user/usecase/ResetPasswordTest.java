package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.user.command.ResetPasswordCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.PasswordResetOtpPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.valueobject.Email;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordResetOtpPort passwordResetOtpPort;
    @Mock
    private EncoderPort encoderPort;

    @InjectMocks
    private ResetPasswordUseCase resetPasswordUseCase;

    @Test
    @DisplayName("UTCID01 - Đặt lại mật khẩu thành công khi thông tin và token hợp lệ")
    void UTCID01_ResetPasswordSuccess() {
        // Arrange
        String emailStr = "user@example.com";
        String tokenStr = "valid-reset-token";

        ResetPasswordCommand command = new ResetPasswordCommand(
                emailStr,
                tokenStr,
                "NewPassword123@",
                "NewPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .hashPassword("$2a$10$oldHashedPassword")
                .isEmailVerified(false)
                .build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.verifyResetToken(emailStr, tokenStr)).thenReturn(true);
        when(encoderPort.matches("NewPassword123@", "$2a$10$oldHashedPassword")).thenReturn(false);
        when(encoderPort.hashPassword("NewPassword123@")).thenReturn("$2a$10$newHashedPassword");

        // Act
        assertDoesNotThrow(() -> resetPasswordUseCase.resetPassword(command));

        // Assert
        assertTrue(user.isEmailVerified());
        assertEquals("$2a$10$newHashedPassword", user.getHashPassword());
        verify(userRepository, times(1)).save(user);
        verify(passwordResetOtpPort, times(1)).removeResetToken(emailStr);
    }

    @Test
    @DisplayName("UTCID02 - Đặt lại mật khẩu thất bại khi mật khẩu mới và xác nhận mật khẩu không khớp")
    void UTCID02_NewPasswordAndConfirmPasswordNotMatch() {
        // Arrange
        ResetPasswordCommand command = new ResetPasswordCommand(
                "user@example.com",
                "valid-token",
                "NewPassword123@",
                "DifferentPassword123@"
        );

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resetPasswordUseCase.resetPassword(command)
        );

        assertEquals(UserErrorCode.USER_PASSWORD_NOT_MATCH, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_PASSWORD_NOT_MATCH_DETAIL, exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("UTCID03 - Đặt lại mật khẩu thất bại khi không tìm thấy người dùng với email tương ứng")
    void UTCID03_UserEmailNotFound() {
        // Arrange
        String emailStr = "unknown@example.com";
        ResetPasswordCommand command = new ResetPasswordCommand(
                emailStr,
                "valid-token",
                "NewPassword123@",
                "NewPassword123@"
        );

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resetPasswordUseCase.resetPassword(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_NOT_FOUND, exception.getMessage());
        verify(passwordResetOtpPort, never()).verifyResetToken(any(), any());
    }

    @Test
    @DisplayName("UTCID04 - Đặt lại mật khẩu thất bại khi token xác thực không hợp lệ hoặc hết hạn")
    void UTCID04_InvalidResetToken() {
        // Arrange
        String emailStr = "user@example.com";
        String invalidTokenStr = "invalid-token";
        ResetPasswordCommand command = new ResetPasswordCommand(
                emailStr,
                invalidTokenStr,
                "NewPassword123@",
                "NewPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.verifyResetToken(emailStr, invalidTokenStr)).thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resetPasswordUseCase.resetPassword(command)
        );

        assertEquals(UserErrorCode.USER_INVALID_RESET_TOKEN, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_INVALID_RESET_TOKEN_DETAIL, exception.getMessage());
        verify(encoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID05 - Đặt lại mật khẩu thất bại khi mật khẩu mới trùng với mật khẩu cũ")
    void UTCID05_NewPasswordSameAsOldPassword() {
        // Arrange
        String emailStr = "user@example.com";
        String tokenStr = "valid-token";
        ResetPasswordCommand command = new ResetPasswordCommand(
                emailStr,
                tokenStr,
                "OldPassword123@",
                "OldPassword123@"
        );

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.verifyResetToken(emailStr, tokenStr)).thenReturn(true);
        when(encoderPort.matches("OldPassword123@", "$2a$10$oldHashedPassword")).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resetPasswordUseCase.resetPassword(command)
        );

        assertEquals(UserErrorCode.USER_PASSWORD_SAME_AS_OLD, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_PASSWORD_SAME_AS_OLD_DETAIL, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("UTCID06 - Đặt lại mật khẩu thất bại khi mật khẩu mới không đúng định dạng (Boundary)")
    void UTCID06_NewPasswordInvalidFormat() {
        // Arrange
        String emailStr = "user@example.com";
        String tokenStr = "valid-token";
        ResetPasswordCommand command = new ResetPasswordCommand(
                emailStr,
                tokenStr,
                "123", // invalid password format
                "123"
        );

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .hashPassword("$2a$10$oldHashedPassword")
                .build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.verifyResetToken(emailStr, tokenStr)).thenReturn(true);
        when(encoderPort.matches("123", "$2a$10$oldHashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(
                DomainException.class,
                () -> resetPasswordUseCase.resetPassword(command)
        );

        verify(userRepository, never()).save(any());
    }
}
