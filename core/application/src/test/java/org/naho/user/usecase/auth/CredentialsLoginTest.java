package org.naho.user.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserSessionDomainErrorCode;
import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.usecase.AuthUseCase;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private UserResultMapper userResultMapper;

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

    @InjectMocks
    private AuthUseCase authUseCase;

    private void mockTransaction() {
        when(transactionPort.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<?> action = invocation.getArgument(0);
                    return action.get();
                });
    }

    @Test
    void UTCID01_Should_LoginSuccessfully_When_AllFieldsValid() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                "device-1",
                "Mozilla",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed_pwd")
                .status(UserStatus.ACTIVE)
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult(
                "refresh_cookie",
                "refresh_token",
                "refresh-value",
                now.plusSeconds(3600),
                3600L
        );
        TokenResult accessToken = new TokenResult(
                "access_cookie",
                "access_token",
                "access-value",
                now.plusSeconds(600),
                600L
        );

        mockTransaction();

        when(userRepositoryPort.findByUsernameOrEmail("admin"))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches("Admin@123", "hashed_pwd"))
                .thenReturn(true);

        when(tokenServicePort.generateRefreshToken(any(Instant.class)))
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken("refresh-value"))
                .thenReturn("hashed-refresh-value");

        when(tokenServicePort.getAccessTokenExpiry(any(Instant.class)))
                .thenReturn(now.plusSeconds(600));

        when(userSessionRepositoryPort.save(any(UserSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(tokenServicePort.generateAccessToken(any(UserSession.class)))
                .thenReturn(accessToken);

        // Act (When)
        LoginResult result = authUseCase.credentialsLogin(command);

        // Assert (Then)
        assertNotNull(result);
        assertEquals(accessToken, result.accessToken());
        assertEquals(refreshToken, result.refreshToken());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail("admin");

        verify(encoderPort, times(1))
                .matches("Admin@123", "hashed_pwd");

        verify(userSessionServicePort, times(1))
                .revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

        verify(userSessionRepositoryPort, times(1))
                .save(argThat(session ->
                        session.getUserId().equals(1L) &&
                                session.getHashRefreshToken().equals("hashed-refresh-value") &&
                                session.getDeviceId().equals("device-1") &&
                                session.getUserAgent().equals("Mozilla") &&
                                session.getIpAddress().equals("127.0.0.1")
                ));

        verify(userSessionEventPublisherPort, times(1))
                .publishForceLogoutEvent(argThat(forceCommand ->
                        forceCommand.userId().equals(1L) &&
                                forceCommand.reason().equals(SessionRevokedReason.LOGIN_ON_OTHER_DEVICE)
                ));

        verifyNoMoreInteractions(
                userRepositoryPort,
                encoderPort,
                tokenServicePort,
                userSessionRepositoryPort,
                userSessionServicePort,
                userSessionEventPublisherPort
        );
        verifyNoInteractions(userResultMapper, roleRepositoryPort);
    }

    @Test
    void UTCID02_Should_ThrowException_When_UserNotFound() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "unknown",
                "Admin@123",
                null,
                null,
                null
        );

        mockTransaction();

        when(userRepositoryPort.findByUsernameOrEmail("unknown"))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail("unknown");

        verifyNoMoreInteractions(userRepositoryPort);
        verifyNoInteractions(
                encoderPort,
                tokenServicePort,
                userResultMapper,
                userSessionRepositoryPort,
                roleRepositoryPort,
                userSessionServicePort,
                userSessionEventPublisherPort
        );
    }

    @Test
    void UTCID03_Should_ThrowException_When_PasswordIncorrect() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "wrong-password",
                "device-1",
                null,
                null
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed_pwd")
                .status(UserStatus.ACTIVE)
                .build();

        mockTransaction();

        when(userRepositoryPort.findByUsernameOrEmail("admin"))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches("wrong-password", "hashed_pwd"))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail("admin");

        verify(encoderPort, times(1))
                .matches("wrong-password", "hashed_pwd");

        verifyNoMoreInteractions(userRepositoryPort, encoderPort);
        verifyNoInteractions(
                tokenServicePort,
                userResultMapper,
                userSessionRepositoryPort,
                roleRepositoryPort,
                userSessionServicePort,
                userSessionEventPublisherPort
        );
    }

    @Test
    void UTCID04_Should_ThrowException_When_UserInactive() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                "device-1",
                null,
                null
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed_pwd")
                .status(UserStatus.UNACTIVE)
                .build();

        mockTransaction();

        when(userRepositoryPort.findByUsernameOrEmail("admin"))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches("Admin@123", "hashed_pwd"))
                .thenReturn(true);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail("admin");

        verify(encoderPort, times(1))
                .matches("Admin@123", "hashed_pwd");

        verifyNoMoreInteractions(userRepositoryPort, encoderPort);
        verifyNoInteractions(
                tokenServicePort,
                userResultMapper,
                userSessionRepositoryPort,
                roleRepositoryPort,
                userSessionServicePort,
                userSessionEventPublisherPort
            );
    }

    @Test
    void UTCID05_Should_ThrowException_When_DeviceIdNullOrEmpty() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                null,
                null,
                null
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed_pwd")
                .status(UserStatus.ACTIVE)
                .build();

        Instant now = Instant.now();
        TokenResult refreshToken = new TokenResult(
                "refresh_cookie",
                "refresh_token",
                "refresh-value",
                now.plusSeconds(3600),
                3600L
        );

        mockTransaction();

        when(userRepositoryPort.findByUsernameOrEmail("admin"))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches("Admin@123", "hashed_pwd"))
                .thenReturn(true);

        when(tokenServicePort.generateRefreshToken(any(Instant.class)))
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken("refresh-value"))
                .thenReturn("hashed-refresh-value");

        when(tokenServicePort.getAccessTokenExpiry(any(Instant.class)))
                .thenReturn(now.plusSeconds(600));

        // Act (When) & Assert (Then)
        DomainException exception = assertThrows(
                DomainException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserSessionDomainErrorCode.USER_SESSION_DEVICE_ID_NOT_VALID, exception.getErrorCode());
        assertEquals(UserSessionDetailMessageKey.USER_SESSION_DEVICE_ID_BLANK, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail("admin");

        verify(encoderPort, times(1))
                .matches("Admin@123", "hashed_pwd");

        verify(userSessionServicePort, times(1))
                .revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

        verify(userSessionRepositoryPort, never())
                .save(any(UserSession.class));

        verifyNoMoreInteractions(
                userRepositoryPort,
                encoderPort,
                tokenServicePort,
                userSessionServicePort
        );
        verifyNoInteractions(
                userResultMapper,
                roleRepositoryPort,
                userSessionRepositoryPort,
                userSessionEventPublisherPort
        );
    }
}
