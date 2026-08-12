package org.naho.user.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.email.port.out.EmailPort;
import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.LogoutCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.*;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutTest {

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

    @Test
    @DisplayName("UTCID01 - Đăng xuất thành công khi thông tin command hợp lệ")
    void UTCID01_LogoutSuccess() {
        // Arrange
        LogoutCommand command = new LogoutCommand(1L, 100L);

        // Act
        authUseCase.logout(command);

        // Assert
        verify(userSessionRepositoryPort, times(1)).revokeActiveSessionsByUserIdAndUserSessionId(
                eq(1L),
                eq(100L),
                any(Instant.class),
                eq(SessionRevokedReason.USER_LOGOUT)
        );
    }

    @Test
    @DisplayName("UTCID02 - Đăng xuất thất bại khi command là null")
    void UTCID02_LogoutCommandNull() {
        // Arrange
        LogoutCommand command = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.logout(command)
        );

        assertEquals(UserErrorCode.USER_UNAUTHORIZED, exception.getErrorCode());
        assertEquals(UserTitleMessageKey.USER_UNAUTHORIZED_TITLE, exception.getMessage());
        verify(userSessionRepositoryPort, never()).revokeActiveSessionsByUserIdAndUserSessionId(
                anyLong(),
                anyLong(),
                any(Instant.class),
                any(SessionRevokedReason.class)
        );
    }

    @Test
    @DisplayName("UTCID03 - Đăng xuất thất bại khi userId trong command là null")
    void UTCID03_LogoutUserIdNull() {
        // Arrange
        LogoutCommand command = new LogoutCommand(null, 100L);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.logout(command)
        );

        assertEquals(UserErrorCode.USER_UNAUTHORIZED, exception.getErrorCode());
        assertEquals(UserTitleMessageKey.USER_UNAUTHORIZED_TITLE, exception.getMessage());
        verify(userSessionRepositoryPort, never()).revokeActiveSessionsByUserIdAndUserSessionId(
                anyLong(),
                anyLong(),
                any(Instant.class),
                any(SessionRevokedReason.class)
        );
    }

    @Test
    @DisplayName("UTCID04 - Đăng xuất thất bại khi userSessionId trong command là null")
    void UTCID04_LogoutUserSessionIdNull() {
        // Arrange
        LogoutCommand command = new LogoutCommand(1L, null);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.logout(command)
        );

        assertEquals(UserErrorCode.USER_UNAUTHORIZED, exception.getErrorCode());
        assertEquals(UserTitleMessageKey.USER_UNAUTHORIZED_TITLE, exception.getMessage());
        verify(userSessionRepositoryPort, never()).revokeActiveSessionsByUserIdAndUserSessionId(
                anyLong(),
                anyLong(),
                any(Instant.class),
                any(SessionRevokedReason.class)
        );
    }
}
