package org.naho.user.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.RoleDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.shared.port.out.EmailPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserSessionDomainErrorCode;
import org.naho.user.model.AuthProvider;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.AuthProviderName;
import org.naho.user.type.RoleName;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleLoginTest {

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
    @DisplayName("UTCID01 - Đăng nhập thành công với Google khi tài khoản đã tồn tại (Google providerUserId)")
    void UTCID01_LoginSuccess_ExistingGoogleUser() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-12345")
                .email("googleuser@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-001")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        User user = User.builder()
                .id(1L)
                .username(Username.of("googleuser"))
                .email(Email.of("googleuser@example.com"))
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .roleIds(List.of(1L))
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);
        TokenResult accessToken = new TokenResult("accessToken", "access_token", "access-token-value", now.plusSeconds(3600), 3600L);

        UserSession savedSession = UserSession.builder()
                .id(100L)
                .userId(1L)
                .hashRefreshToken("hashed-refresh-token")
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ipAddress(command.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(now.plusSeconds(3600))
                .lastUsedAt(now)
                .build();

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-12345", AuthProviderName.GOOGLE))
                .thenReturn(Optional.of(user));
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");
        when(userSessionRepositoryPort.save(any(UserSession.class))).thenReturn(savedSession);
        when(tokenServicePort.generateAccessToken(savedSession)).thenReturn(accessToken);

        // Act
        LoginResult result = authUseCase.googleLogin(command);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(userSessionServicePort).revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);
        verify(userSessionRepositoryPort).save(any(UserSession.class));
        verify(userSessionEventPublisherPort).publishForceLogoutEvent(any(ForceLogoutCommand.class));
    }

    @Test
    @DisplayName("UTCID02 - Đăng nhập thành công với Google khi tạo người dùng mới (chưa tồn tại Google ID và email)")
    void UTCID02_LoginSuccess_NewUserCreated() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-67890")
                .email("newgoogleuser@example.com")
                .fullName("New Google User")
                .pictureUrl("https://example.com/avatar_new.jpg")
                .deviceId("device-002")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        Role learnerRole = Role.builder()
                .id(2L)
                .roleName(RoleName.LEARNER)
                .build();

        User createdUser = User.builder()
                .id(10L)
                .email(Email.of("newgoogleuser@example.com"))
                .fullName("New Google User")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .roleIds(List.of(2L))
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);
        TokenResult accessToken = new TokenResult("accessToken", "access_token", "access-token-value", now.plusSeconds(3600), 3600L);

        UserSession savedSession = UserSession.builder()
                .id(101L)
                .userId(10L)
                .hashRefreshToken("hashed-refresh-token")
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ipAddress(command.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(now.plusSeconds(3600))
                .lastUsedAt(now)
                .build();

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-67890", AuthProviderName.GOOGLE))
                .thenReturn(Optional.empty());
        when(userRepositoryPort.findByEmail("newgoogleuser@example.com"))
                .thenReturn(Optional.empty());
        when(roleRepositoryPort.findByName(RoleName.LEARNER))
                .thenReturn(Optional.of(learnerRole));
        when(userRepositoryPort.createNew(any(User.class), any(AuthProvider.class)))
                .thenReturn(createdUser);
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");
        when(userSessionRepositoryPort.save(any(UserSession.class))).thenReturn(savedSession);
        when(tokenServicePort.generateAccessToken(savedSession)).thenReturn(accessToken);

        // Act
        LoginResult result = authUseCase.googleLogin(command);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(crudUserLearningProgressInputPort).initUserLearningProgress(10L);
        verify(userSessionServicePort).revokeAllSessionsByUserId(10L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);
        verify(userSessionRepositoryPort).save(any(UserSession.class));
    }

    @Test
    @DisplayName("UTCID03 - Đăng nhập thành công với Google khi liên kết với tài khoản email đã tồn tại")
    void UTCID03_LoginSuccess_ExistingEmailUserLinked() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-99999")
                .email("existingemail@example.com")
                .fullName("Existing User")
                .pictureUrl("https://example.com/avatar_existing.jpg")
                .deviceId("device-003")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        User existingEmailUser = User.builder()
                .id(5L)
                .email(Email.of("existingemail@example.com"))
                .fullName("Existing User")
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .roleIds(List.of(2L))
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);
        TokenResult accessToken = new TokenResult("accessToken", "access_token", "access-token-value", now.plusSeconds(3600), 3600L);

        UserSession savedSession = UserSession.builder()
                .id(102L)
                .userId(5L)
                .hashRefreshToken("hashed-refresh-token")
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ipAddress(command.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(now.plusSeconds(3600))
                .lastUsedAt(now)
                .build();

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-99999", AuthProviderName.GOOGLE))
                .thenReturn(Optional.empty());
        when(userRepositoryPort.findByEmail("existingemail@example.com"))
                .thenReturn(Optional.of(existingEmailUser));
        when(userRepositoryPort.createNew(eq(existingEmailUser), any(AuthProvider.class)))
                .thenReturn(existingEmailUser);
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");
        when(userSessionRepositoryPort.save(any(UserSession.class))).thenReturn(savedSession);
        when(tokenServicePort.generateAccessToken(savedSession)).thenReturn(accessToken);

        // Act
        LoginResult result = authUseCase.googleLogin(command);

        // Assert
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(roleRepositoryPort, never()).findByName(any());
        verify(crudUserLearningProgressInputPort, never()).initUserLearningProgress(anyLong());
        verify(userSessionServicePort).revokeAllSessionsByUserId(5L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);
    }

    @Test
    @DisplayName("UTCID04 - Đăng nhập thất bại với Google khi tạo user mới nhưng không tìm thấy vai trò LEARNER")
    void UTCID04_LearnerRoleNotFound() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-00000")
                .email("noroleuser@example.com")
                .fullName("No Role User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-004")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-00000", AuthProviderName.GOOGLE))
                .thenReturn(Optional.empty());
        when(userRepositoryPort.findByEmail("noroleuser@example.com"))
                .thenReturn(Optional.empty());
        when(roleRepositoryPort.findByName(RoleName.LEARNER))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        assertEquals(RoleDetailMessageKey.ROLE_ROLE_NAME_NOT_FOUND, exception.getMessage());
        verify(userRepositoryPort, never()).createNew(any(), any());
    }

    @Test
    @DisplayName("UTCID05 - Đăng nhập thất bại với Google khi tài khoản người dùng chưa được kích hoạt (UNACTIVE)")
    void UTCID05_UserAccountNotActive() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-inactive")
                .email("inactiveuser@example.com")
                .fullName("Inactive User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-005")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        User inactiveUser = User.builder()
                .id(7L)
                .email(Email.of("inactiveuser@example.com"))
                .fullName("Inactive User")
                .status(UserStatus.UNACTIVE)
                .isEmailVerified(true)
                .roleIds(List.of(1L))
                .build();

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-inactive", AuthProviderName.GOOGLE))
                .thenReturn(Optional.of(inactiveUser));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());
        verify(tokenServicePort, never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("UTCID06 - Đăng nhập thất bại với Google khi deviceId rỗng/null (Boundary Exception)")
    void UTCID06_DeviceIdBlank() {
        // Arrange
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("google-sub-12345")
                .email("googleuser@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("") // blank deviceId
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .build();

        User user = User.builder()
                .id(1L)
                .username(Username.of("googleuser"))
                .email(Email.of("googleuser@example.com"))
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .roleIds(List.of(1L))
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult("refreshToken", "refresh_token", "refresh-token-value", now.plusSeconds(86400), 86400L);

        when(userRepositoryPort.findByProviderUserIdAndProviderName("google-sub-12345", AuthProviderName.GOOGLE))
                .thenReturn(Optional.of(user));
        when(tokenServicePort.generateRefreshToken(any())).thenReturn(refreshToken);
        when(encoderPort.hashRefreshToken("refresh-token-value")).thenReturn("hashed-refresh-token");

        // Act & Assert
        DomainException exception = assertThrows(
                DomainException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(UserSessionDomainErrorCode.USER_SESSION_DEVICE_ID_NOT_VALID, exception.getErrorCode());
        assertEquals(UserSessionDetailMessageKey.USER_SESSION_DEVICE_ID_BLANK, exception.getMessage());
    }
}
