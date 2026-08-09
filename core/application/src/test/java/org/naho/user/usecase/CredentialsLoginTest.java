package org.naho.user.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.shared.port.out.EmailPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserSessionDomainErrorCode;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialsLoginTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private EncoderPort encoderPort;
    @Mock
    private TokenServicePort tokenServicePort;
    @Mock
    private UserSessionRepositoryPort userSessionRepositoryPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private UserSessionServicePort userSessionServicePort;
    @Mock
    private UserSessionEventPublisherPort userSessionEventPublisherPort;
    @Mock
    private CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;
    @Mock
    private OtpPort otpPort;
    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
    }

    @Test
    @DisplayName("UTCID01 - Đăng nhập thành công khi thông tin chính xác, tài khoản active và đã xác thực email")
    void UTCID01_LoginSuccess() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "vuongtruc",
                "Password123@",
                "device-001",
                "Chrome/120.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .username(Username.of("vuongtruc"))
                .email(Email.of("vuongtruc@example.com"))
                .hashPassword("$2a$10$hashedPassword")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .roleId(1L)
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);
        TokenResult accessToken = new TokenResult("accessToken", "access_token", "access-token-value", now.plusSeconds(3600), 3600L);

        UserSession savedSession = UserSession.builder()
                .id(100L)
                .userId(1L)
                .hashRefreshToken("hashed-refresh-token")
                .deviceId(command.deviceId())
                .userAgent(command.userAgent())
                .ipAddress(command.ipAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(now.plusSeconds(3600))
                .lastUsedAt(now)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail("vuongtruc")).thenReturn(Optional.of(user));
        when(encoderPort.matches("Password123@", "$2a$10$hashedPassword")).thenReturn(true);
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");
        when(userSessionRepositoryPort.save(any(UserSession.class))).thenReturn(savedSession);
        when(tokenServicePort.generateAccessToken(savedSession)).thenReturn(accessToken);

        // Act
        LoginResult result = authUseCase.credentialsLogin(command);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(userSessionServicePort).revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);
        verify(userSessionRepositoryPort).save(any(UserSession.class));
        verify(userSessionEventPublisherPort).publishForceLogoutEvent(any(ForceLogoutCommand.class));
    }

    @Test
    @DisplayName("UTCID02 - Đăng nhập thất bại khi không tìm thấy người dùng")
    void UTCID02_UserNotFound() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "unknown_user",
                "Password123@",
                "device-001",
                "Chrome/120.0",
                "127.0.0.1"
        );

        when(userRepositoryPort.findByUsernameOrEmail("unknown_user")).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());
        verify(encoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("UTCID03 - Đăng nhập thất bại khi mật khẩu không đúng")
    void UTCID03_WrongPassword() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "vuongtruc",
                "WrongPassword123@",
                "device-001",
                "Chrome/120.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .username(Username.of("vuongtruc"))
                .email(Email.of("vuongtruc@example.com"))
                .hashPassword("$2a$10$hashedPassword")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail("vuongtruc")).thenReturn(Optional.of(user));
        when(encoderPort.matches("WrongPassword123@", "$2a$10$hashedPassword")).thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Đăng nhập thất bại khi tài khoản chưa được kích hoạt (UNACTIVE)")
    void UTCID04_UserAccountNotActive() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "vuongtruc",
                "Password123@",
                "device-001",
                "Chrome/120.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .username(Username.of("vuongtruc"))
                .email(Email.of("vuongtruc@example.com"))
                .hashPassword("$2a$10$hashedPassword")
                .status(UserStatus.UNACTIVE)
                .isEmailVerified(true)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail("vuongtruc")).thenReturn(Optional.of(user));
        when(encoderPort.matches("Password123@", "$2a$10$hashedPassword")).thenReturn(true);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID05 - Đăng nhập thất bại khi email chưa được xác thực (tự động tạo và gửi OTP)")
    void UTCID05_UserEmailUnverified() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "vuongtruc",
                "Password123@",
                "device-001",
                "Chrome/120.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .username(Username.of("vuongtruc"))
                .email(Email.of("vuongtruc@example.com"))
                .hashPassword("$2a$10$hashedPassword")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(false)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail("vuongtruc")).thenReturn(Optional.of(user));
        when(encoderPort.matches("Password123@", "$2a$10$hashedPassword")).thenReturn(true);
        when(otpPort.hasValidOtp("vuongtruc@example.com")).thenReturn(false);
        when(otpPort.generateOtp()).thenReturn("123456");

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_EMAIL_UNVERIFIED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_UNVERIFIED_DETAIL, exception.getMessage());
        verify(otpPort).saveOtp("vuongtruc@example.com", "123456");
        verify(emailPort).sendOtpEmail(eq("vuongtruc@example.com"), any(), eq("123456"));
    }

    @Test
    @DisplayName("UTCID06 - Đăng nhập thất bại khi deviceId bị rỗng/null (Boundary Exception)")
    void UTCID06_DeviceIdBlank() {
        // Arrange
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "vuongtruc",
                "Password123@",
                "", // deviceId blank
                "Chrome/120.0",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .username(Username.of("vuongtruc"))
                .email(Email.of("vuongtruc@example.com"))
                .hashPassword("$2a$10$hashedPassword")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);

        when(userRepositoryPort.findByUsernameOrEmail("vuongtruc")).thenReturn(Optional.of(user));
        when(encoderPort.matches("Password123@", "$2a$10$hashedPassword")).thenReturn(true);
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");

        // Act & Assert
        DomainException exception = assertThrows(
                DomainException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserSessionDomainErrorCode.USER_SESSION_DEVICE_ID_NOT_VALID, exception.getErrorCode());
        assertEquals(UserSessionDetailMessageKey.USER_SESSION_DEVICE_ID_BLANK, exception.getMessage());
    }
}
