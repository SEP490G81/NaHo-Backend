package org.naho.user.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.VerifyEmailCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.valueobject.Email;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyEmailTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private OtpPort otpPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private TokenServicePort tokenServicePort;
    @Mock
    private EncoderPort encoderPort;
    @Mock
    private UserSessionRepositoryPort userSessionRepositoryPort;
    @Mock
    private UserSessionServicePort userSessionServicePort;
    @Mock
    private UserSessionEventPublisherPort userSessionEventPublisherPort;

    @InjectMocks
    private VerifyEmailUseCase verifyEmailUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
    }

    @Test
    @DisplayName("UTCID01 - Xác thực email thành công khi OTP hợp lệ")
    void UTCID01_VerifyEmailSuccess() {
        // Arrange
        String emailStr = "user@example.com";
        String otpCode = "123456";
        VerifyEmailCommand command = new VerifyEmailCommand(
                emailStr,
                otpCode,
                "device-123",
                "Mozilla/5.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .email(Email.of(emailStr))
                .isEmailVerified(false)
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-val", now.plusSeconds(3600), 3600L);
        TokenResult accessToken = new TokenResult("accessToken", "access_token", "access-token-val", now.plusSeconds(900), 900L);

        UserSession savedSession = UserSession.builder()
                .id(10L)
                .userId(1L)
                .hashRefreshToken("hashed-refresh-token")
                .deviceId("device-123")
                .userAgent("Mozilla/5.0")
                .ipAddress("127.0.0.1")
                .issuedAt(now)
                .refreshTokenExpiresAt(now.plusSeconds(3600))
                .accessTokenExpiresAt(now.plusSeconds(900))
                .lastUsedAt(now)
                .build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.verifyOtp(emailStr, otpCode)).thenReturn(true);
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken(refreshToken.value())).thenReturn("hashed-refresh-token");
        when(tokenServicePort.getAccessTokenExpiry(any())).thenReturn(now.plusSeconds(900));
        when(userSessionRepositoryPort.save(any())).thenReturn(savedSession);
        when(tokenServicePort.generateAccessToken(savedSession)).thenReturn(accessToken);

        // Act
        LoginResult result = verifyEmailUseCase.verifyEmail(command);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());
        assertTrue(user.isEmailVerified());
        verify(userRepositoryPort, times(1)).save(user);
        verify(otpPort, times(1)).removeOtp(emailStr);
        verify(otpPort, times(1)).clearFailedAttempts(emailStr);
    }

    @Test
    @DisplayName("UTCID02 - Xác thực email thất bại khi không tìm thấy người dùng theo email")
    void UTCID02_UserEmailNotFound() {
        // Arrange
        String emailStr = "unknown@example.com";
        VerifyEmailCommand command = new VerifyEmailCommand(emailStr, "123456", "dev", "agent", "127.0.0.1");

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyEmailUseCase.verifyEmail(command)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_NOT_FOUND, exception.getMessage());
        verify(otpPort, never()).verifyOtp(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Xác thực email thất bại khi email đã được xác thực từ trước")
    void UTCID03_UserEmailAlreadyVerified() {
        // Arrange
        String emailStr = "verified@example.com";
        VerifyEmailCommand command = new VerifyEmailCommand(emailStr, "123456", "dev", "agent", "127.0.0.1");

        User user = User.builder().id(1L).email(Email.of(emailStr)).isEmailVerified(true).build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyEmailUseCase.verifyEmail(command)
        );

        assertEquals(UserErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_ALREADY_VERIFIED, exception.getMessage());
        verify(otpPort, never()).verifyOtp(any(), any());
    }

    @Test
    @DisplayName("UTCID04 - Xác thực email thất bại khi mã OTP không đúng (chưa vượt quá 5 lần sai)")
    void UTCID04_InvalidOtpCode_Under5Attempts() {
        // Arrange
        String emailStr = "user@example.com";
        VerifyEmailCommand command = new VerifyEmailCommand(emailStr, "000000", "dev", "agent", "127.0.0.1");

        User user = User.builder().id(1L).email(Email.of(emailStr)).isEmailVerified(false).build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.verifyOtp(emailStr, "000000")).thenReturn(false);
        when(otpPort.getFailedAttempts(emailStr)).thenReturn(3);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyEmailUseCase.verifyEmail(command)
        );

        assertEquals(UserErrorCode.USER_INVALID_OTP, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_INVALID_OTP_DETAIL, exception.getMessage());
        verify(otpPort, times(1)).incrementFailedAttempts(emailStr);
        verify(otpPort, never()).removeOtp(any());
    }

    @Test
    @DisplayName("UTCID05 - Xác thực email thất bại khi nhập sai OTP quá 5 lần (vượt quá giới hạn thử lại)")
    void UTCID05_InvalidOtpCode_AttemptsExceeded() {
        // Arrange
        String emailStr = "user@example.com";
        VerifyEmailCommand command = new VerifyEmailCommand(emailStr, "000000", "dev", "agent", "127.0.0.1");

        User user = User.builder().id(1L).email(Email.of(emailStr)).isEmailVerified(false).build();

        when(userRepositoryPort.findByEmail(emailStr)).thenReturn(Optional.of(user));
        when(otpPort.verifyOtp(emailStr, "000000")).thenReturn(false);
        when(otpPort.getFailedAttempts(emailStr)).thenReturn(5);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> verifyEmailUseCase.verifyEmail(command)
        );

        assertEquals(UserErrorCode.USER_OTP_ATTEMPTS_EXCEEDED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_OTP_ATTEMPTS_EXCEEDED_DETAIL, exception.getMessage());
        verify(otpPort, times(1)).removeOtp(emailStr);
        verify(otpPort, times(1)).clearFailedAttempts(emailStr);
    }
}
