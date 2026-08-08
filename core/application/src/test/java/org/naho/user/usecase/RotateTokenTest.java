package org.naho.user.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EmailPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;

import java.time.Instant;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RotateTokenTest {

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
    @DisplayName("UTCID01 - Rotate token thành công khi Refresh Token hợp lệ")
    void UTCID01_RotateTokenSuccess() {
        // Arrange
        String rawRefreshToken = "valid-refresh-token";
        String hashedRefreshToken = "hashed-refresh-token";

        Instant now = Instant.now();
        UserSession existingSession = UserSession.builder()
                .id(100L)
                .userId(1L)
                .hashRefreshToken(hashedRefreshToken)
                .deviceId("device-001")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .issuedAt(now.minusSeconds(3600))
                .refreshTokenExpiresAt(now.plusSeconds(86400))
                .accessTokenExpiresAt(now.plusSeconds(1800))
                .lastUsedAt(now.minusSeconds(3600))
                .build();

        TokenResult newRefreshToken = new TokenResult("newRefreshToken", "refresh_token", "new-refresh-token-value", now.plusSeconds(86400), 86400L);
        TokenResult newAccessToken = new TokenResult("newAccessToken", "access_token", "new-access-token-value", now.plusSeconds(3600), 3600L);

        UserSession savedNewSession = UserSession.builder()
                .id(101L)
                .userId(1L)
                .hashRefreshToken("new-hashed-refresh-token")
                .deviceId("device-001")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .issuedAt(now)
                .refreshTokenExpiresAt(newRefreshToken.expiresAt())
                .accessTokenExpiresAt(now.plusSeconds(3600))
                .lastUsedAt(now)
                .build();

        when(encoderPort.hashRefreshToken(rawRefreshToken)).thenReturn(hashedRefreshToken);
        when(userSessionRepositoryPort.findByHashRefreshToken(hashedRefreshToken)).thenReturn(existingSession);
        doNothing().when(userSessionRepositoryPort).verifyUserSession(eq(existingSession), any(Instant.class));
        when(tokenServicePort.generateRefreshToken(any(Instant.class))).thenReturn(newRefreshToken);
        when(encoderPort.hashRefreshToken("new-refresh-token-value")).thenReturn("new-hashed-refresh-token");
        when(userSessionRepositoryPort.save(any(UserSession.class))).thenReturn(savedNewSession);
        when(tokenServicePort.generateAccessToken(savedNewSession)).thenReturn(newAccessToken);

        // Act
        LoginResult result = authUseCase.rotateToken(rawRefreshToken);

        // Assert
        assertNotNull(result);
        assertEquals(newAccessToken, result.accessToken());
        assertEquals(newRefreshToken, result.refreshToken());
        verify(userSessionRepositoryPort, times(2)).save(any(UserSession.class));
    }

    @Test
    @DisplayName("UTCID02 - Rotate token thất bại khi không tìm thấy Refresh Token")
    void UTCID02_RefreshTokenNotFound() {
        // Arrange
        String rawRefreshToken = "non-existent-token";
        String hashedRefreshToken = "hashed-non-existent-token";

        when(encoderPort.hashRefreshToken(rawRefreshToken)).thenReturn(hashedRefreshToken);
        when(userSessionRepositoryPort.findByHashRefreshToken(hashedRefreshToken))
                .thenThrow(new ApplicationException(
                        UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                        UserDetailMessageKey.USER_REFRESH_TOKEN_NOT_FOUND
                ));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.rotateToken(rawRefreshToken)
        );

        assertEquals(UserErrorCode.USER_INVALID_REFRESH_TOKEN, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_REFRESH_TOKEN_NOT_FOUND, exception.getMessage());
        verify(userSessionRepositoryPort, never()).verifyUserSession(any(), any());
    }

    @Test
    @DisplayName("UTCID03 - Rotate token thất bại khi Refresh Token đã hết hạn")
    void UTCID03_RefreshTokenExpired() {
        // Arrange
        String rawRefreshToken = "expired-token";
        String hashedRefreshToken = "hashed-expired-token";
        Instant now = Instant.now();

        UserSession expiredSession = UserSession.builder()
                .id(100L)
                .userId(1L)
                .hashRefreshToken(hashedRefreshToken)
                .deviceId("device-001")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .issuedAt(now.minusSeconds(3600))
                .refreshTokenExpiresAt(now.minusSeconds(60))
                .accessTokenExpiresAt(now.minusSeconds(1800))
                .lastUsedAt(now.minusSeconds(3600))
                .build();

        when(encoderPort.hashRefreshToken(rawRefreshToken)).thenReturn(hashedRefreshToken);
        when(userSessionRepositoryPort.findByHashRefreshToken(hashedRefreshToken)).thenReturn(expiredSession);
        doThrow(new ApplicationException(
                UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                UserDetailMessageKey.USER_REFRESH_TOKEN_EXPIRED
        )).when(userSessionRepositoryPort).verifyUserSession(eq(expiredSession), any(Instant.class));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.rotateToken(rawRefreshToken)
        );

        assertEquals(UserErrorCode.USER_INVALID_REFRESH_TOKEN, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_REFRESH_TOKEN_EXPIRED, exception.getMessage());
    }

    @Test
    @DisplayName("UTCID04 - Rotate token thất bại khi Refresh Token đã bị thu hồi (Token Reuse)")
    void UTCID04_RefreshTokenRevoked() {
        // Arrange
        String rawRefreshToken = "revoked-token";
        String hashedRefreshToken = "hashed-revoked-token";
        Instant now = Instant.now();

        UserSession revokedSession = UserSession.builder()
                .id(100L)
                .userId(1L)
                .hashRefreshToken(hashedRefreshToken)
                .deviceId("device-001")
                .userAgent("Chrome/120.0")
                .ipAddress("127.0.0.1")
                .issuedAt(now.minusSeconds(3600))
                .refreshTokenExpiresAt(now.plusSeconds(86400))
                .accessTokenExpiresAt(now.plusSeconds(1800))
                .lastUsedAt(now.minusSeconds(3600))
                .build();

        when(encoderPort.hashRefreshToken(rawRefreshToken)).thenReturn(hashedRefreshToken);
        when(userSessionRepositoryPort.findByHashRefreshToken(hashedRefreshToken)).thenReturn(revokedSession);
        doThrow(new ApplicationException(
                UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                UserDetailMessageKey.USER_REFRESH_TOKEN_REVOKED
        )).when(userSessionRepositoryPort).verifyUserSession(eq(revokedSession), any(Instant.class));

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.rotateToken(rawRefreshToken)
        );

        assertEquals(UserErrorCode.USER_INVALID_REFRESH_TOKEN, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_REFRESH_TOKEN_REVOKED, exception.getMessage());
    }
}
