package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.VerifyForgotPasswordOtpCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.PasswordResetOtpPort;
import org.naho.user.port.out.UserRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyForgotPasswordOtpTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordResetOtpPort passwordResetOtpPort;

    @InjectMocks
    private VerifyForgotPasswordOtpUseCase verifyForgotPasswordOtpUseCase;

    @Test
    @DisplayName("UTCID01 - Xác thực OTP quên mật khẩu thành công khi OTP đúng và chưa vượt quá số lần thử")
    void UTCID01_VerifyOtpSuccess() {
        // Arrange
        String emailStr = "user@example.com";
        String otpCode = "123456";
        VerifyForgotPasswordOtpCommand command = new VerifyForgotPasswordOtpCommand(emailStr, otpCode);

        User user = User.builder().id(1L).build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.getFailedAttempts(emailStr)).thenReturn(2);
        when(passwordResetOtpPort.verifyOtp(emailStr, otpCode)).thenReturn(true);

        // Act
        String resetToken = verifyForgotPasswordOtpUseCase.verifyOtp(command);

        // Assert
        assertNotNull(resetToken);
        assertFalse(resetToken.isBlank());
        verify(passwordResetOtpPort, times(1)).removeOtp(emailStr);
        verify(passwordResetOtpPort, times(1)).clearFailedAttempts(emailStr);
        verify(passwordResetOtpPort, times(1)).saveResetToken(eq(emailStr), eq(resetToken));
    }

    @Test
    @DisplayName("UTCID02 - Xác thực OTP quên mật khẩu thất bại khi không tìm thấy người dùng theo email")
    void UTCID02_UserEmailNotFound() {
        // Arrange
        String emailStr = "unknown@example.com";
        VerifyForgotPasswordOtpCommand command = new VerifyForgotPasswordOtpCommand(emailStr, "123456");

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyForgotPasswordOtpUseCase.verifyOtp(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_NOT_FOUND, exception.getMessage());
        verify(passwordResetOtpPort, never()).getFailedAttempts(any());
    }

    @Test
    @DisplayName("UTCID03 - Xác thực OTP quên mật khẩu thất bại khi đã vượt quá 5 lần thử sai trước đó")
    void UTCID03_AttemptsExceeded() {
        // Arrange
        String emailStr = "user@example.com";
        VerifyForgotPasswordOtpCommand command = new VerifyForgotPasswordOtpCommand(emailStr, "123456");

        User user = User.builder().id(1L).build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.getFailedAttempts(emailStr)).thenReturn(5);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyForgotPasswordOtpUseCase.verifyOtp(command)
        );

        assertEquals(UserErrorCode.USER_OTP_ATTEMPTS_EXCEEDED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_OTP_ATTEMPTS_EXCEEDED_DETAIL, exception.getMessage());
        verify(passwordResetOtpPort, times(1)).removeOtp(emailStr);
        verify(passwordResetOtpPort, times(1)).clearFailedAttempts(emailStr);
        verify(passwordResetOtpPort, never()).verifyOtp(any(), any());
    }

    @Test
    @DisplayName("UTCID04 - Xác thực OTP quên mật khẩu thất bại khi nhập sai mã OTP")
    void UTCID04_InvalidOtpCode() {
        // Arrange
        String emailStr = "user@example.com";
        String wrongOtp = "000000";
        VerifyForgotPasswordOtpCommand command = new VerifyForgotPasswordOtpCommand(emailStr, wrongOtp);

        User user = User.builder().id(1L).build();

        when(userRepository.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(passwordResetOtpPort.getFailedAttempts(emailStr)).thenReturn(1);
        when(passwordResetOtpPort.verifyOtp(emailStr, wrongOtp)).thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyForgotPasswordOtpUseCase.verifyOtp(command)
        );

        assertEquals(UserErrorCode.USER_INVALID_OTP, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_INVALID_OTP_DETAIL, exception.getMessage());
        verify(passwordResetOtpPort, times(1)).incrementFailedAttempts(emailStr);
        verify(passwordResetOtpPort, never()).saveResetToken(any(), any());
    }
}
