package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.command.ResendOtpCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.port.out.OtpPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.valueobject.Email;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResendOtpTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private OtpPort otpPort;
    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private ResendOtpUseCase resendOtpUseCase;

    @Test
    @DisplayName("UTCID01 - Gửi lại OTP thành công khi chưa có OTP trước đó")
    void UTCID01_ResendOtpSuccess_NoPreviousOtp() {
        // Arrange
        String emailStr = "user@example.com";
        ResendOtpCommand command = new ResendOtpCommand(emailStr);

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .isEmailVerified(false)
                .build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.hasValidOtp(emailStr)).thenReturn(false);
        when(otpPort.generateOtp()).thenReturn("123456");

        // Act
        assertDoesNotThrow(() -> resendOtpUseCase.resendOtp(command));

        // Assert
        verify(otpPort, times(1)).saveOtp(emailStr, "123456");
        verify(emailPort, times(1)).sendOtpEmail(eq(emailStr), any(), eq("123456"));
    }

    @Test
    @DisplayName("UTCID02 - Gửi lại OTP thành công khi OTP trước đó đã hết thời gian Cooldown (TTL <= 240s)")
    void UTCID02_ResendOtpSuccess_CooldownPassed() {
        // Arrange
        String emailStr = "user@example.com";
        ResendOtpCommand command = new ResendOtpCommand(emailStr);

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .isEmailVerified(false)
                .build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.hasValidOtp(emailStr)).thenReturn(true);
        when(otpPort.getOtpTtlSeconds(emailStr)).thenReturn(200L); // <= 240s
        when(otpPort.generateOtp()).thenReturn("654321");

        // Act
        assertDoesNotThrow(() -> resendOtpUseCase.resendOtp(command));

        // Assert
        verify(otpPort, times(1)).saveOtp(emailStr, "654321");
        verify(emailPort, times(1)).sendOtpEmail(eq(emailStr), any(), eq("654321"));
    }

    @Test
    @DisplayName("UTCID03 - Gửi lại OTP thất bại khi không tìm thấy người dùng với email tương ứng")
    void UTCID03_UserEmailNotFound() {
        // Arrange
        String emailStr = "unknown@example.com";
        ResendOtpCommand command = new ResendOtpCommand(emailStr);

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resendOtpUseCase.resendOtp(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_NOT_FOUND, exception.getMessage());
        verify(otpPort, never()).generateOtp();
    }

    @Test
    @DisplayName("UTCID04 - Gửi lại OTP thất bại khi email của người dùng đã được xác thực trước đó")
    void UTCID04_UserEmailAlreadyVerified() {
        // Arrange
        String emailStr = "verified@example.com";
        ResendOtpCommand command = new ResendOtpCommand(emailStr);

        User verifiedUser = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .isEmailVerified(true)
                .build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(verifiedUser));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resendOtpUseCase.resendOtp(command)
        );

        assertEquals(UserErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_ALREADY_VERIFIED, exception.getMessage());
        verify(otpPort, never()).generateOtp();
    }

    @Test
    @DisplayName("UTCID05 - Gửi lại OTP thất bại khi yêu cầu quá nhanh (vẫn trong Cooldown 1 phút, TTL > 240s)")
    void UTCID05_OtpCooldownActive() {
        // Arrange
        String emailStr = "user@example.com";
        ResendOtpCommand command = new ResendOtpCommand(emailStr);

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .isEmailVerified(false)
                .build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.hasValidOtp(emailStr)).thenReturn(true);
        when(otpPort.getOtpTtlSeconds(emailStr)).thenReturn(280L); // > 240s

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> resendOtpUseCase.resendOtp(command)
        );

        assertEquals(UserErrorCode.USER_OTP_COOLDOWN, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_OTP_COOLDOWN_DETAIL, exception.getMessage());
        verify(otpPort, never()).generateOtp();
    }
}
