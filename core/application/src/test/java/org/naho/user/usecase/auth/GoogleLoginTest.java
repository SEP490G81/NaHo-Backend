package org.naho.user.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserDomainErrorCode;
import org.naho.user.exception.UserSessionDomainErrorCode;
import org.naho.i18n.message.user.RoleDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserSessionDetailMessageKey;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.RoleName;
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
class GoogleLoginTest {

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
        void UTCID01_Should_LoginSuccessfully_When_UserExistsWithProviderId() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                User user = User.builder()
                                .id(1L)
                                .providerId("google_sub123")
                                .status(UserStatus.ACTIVE)
                                .build();

                Instant now = Instant.now();
                TokenResult refreshToken = new TokenResult(
                                "refresh_cookie",
                                "refresh_token",
                                "refresh-value",
                                now.plusSeconds(3600),
                                3600L);
                TokenResult accessToken = new TokenResult(
                                "access_cookie",
                                "access_token",
                                "access-value",
                                now.plusSeconds(600),
                                600L);

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.of(user));

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
                LoginResult result = authUseCase.googleLogin(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(accessToken, result.accessToken());
                assertEquals(refreshToken, result.refreshToken());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userSessionServicePort, times(1))
                                .revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

                verify(userSessionRepositoryPort, times(1))
                                .save(argThat(session -> session.getUserId().equals(1L) &&
                                                session.getHashRefreshToken().equals("hashed-refresh-value") &&
                                                session.getDeviceId().equals("device-1") &&
                                                session.getUserAgent().equals("Mozilla") &&
                                                session.getIpAddress().equals("127.0.0.1")));

                verify(userSessionEventPublisherPort, times(1))
                                .publishForceLogoutEvent(argThat(forceCommand -> forceCommand.userId().equals(1L) &&
                                                forceCommand.reason()
                                                                .equals(SessionRevokedReason.LOGIN_ON_OTHER_DEVICE)));

                verifyNoMoreInteractions(
                                userRepositoryPort,
                                tokenServicePort,
                                encoderPort,
                                userSessionRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
                verifyNoInteractions(userResultMapper, roleRepositoryPort);
        }

        @Test
        void UTCID02_Should_LoginSuccessfully_When_UserExistsWithEmail() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                User user = spy(User.builder()
                                .id(1L)
                                .status(UserStatus.ACTIVE)
                                .build());

                User linkedUser = User.builder()
                                .id(1L)
                                .providerId("google_sub123")
                                .status(UserStatus.ACTIVE)
                                .build();

                Instant now = Instant.now();
                TokenResult refreshToken = new TokenResult(
                                "refresh_cookie",
                                "refresh_token",
                                "refresh-value",
                                now.plusSeconds(3600),
                                3600L);
                TokenResult accessToken = new TokenResult(
                                "access_cookie",
                                "access_token",
                                "access-value",
                                now.plusSeconds(600),
                                600L);

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.empty());

                when(userRepositoryPort.findByEmail("user@example.com"))
                                .thenReturn(Optional.of(user));

                when(userRepositoryPort.save(user))
                                .thenReturn(linkedUser);

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
                LoginResult result = authUseCase.googleLogin(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(accessToken, result.accessToken());
                assertEquals(refreshToken, result.refreshToken());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userRepositoryPort, times(1))
                                .findByEmail("user@example.com");

                verify(user, times(1))
                                .setProviderId("google_sub123");

                verify(userRepositoryPort, times(1))
                                .save(user);

                verify(userSessionServicePort, times(1))
                                .revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

                verify(userSessionRepositoryPort, times(1))
                                .save(any(UserSession.class));

                verify(userSessionEventPublisherPort, times(1))
                                .publishForceLogoutEvent(any(ForceLogoutCommand.class));

                verifyNoMoreInteractions(
                                userRepositoryPort,
                                tokenServicePort,
                                encoderPort,
                                userSessionRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
                verifyNoInteractions(userResultMapper, roleRepositoryPort);
        }

        @Test
        void UTCID03_Should_LoginSuccessfully_When_NewUserRegistration() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                Role role = Role.builder()
                                .id(100L)
                                .roleName(RoleName.LEARNER)
                                .build();

                User newUser = User.builder()
                                .id(2L)
                                .providerId("google_sub123")
                                .status(UserStatus.ACTIVE)
                                .build();

                Instant now = Instant.now();
                TokenResult refreshToken = new TokenResult(
                                "refresh_cookie",
                                "refresh_token",
                                "refresh-value",
                                now.plusSeconds(3600),
                                3600L);
                TokenResult accessToken = new TokenResult(
                                "access_cookie",
                                "access_token",
                                "access-value",
                                now.plusSeconds(600),
                                600L);

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.empty());

                when(userRepositoryPort.findByEmail("user@example.com"))
                                .thenReturn(Optional.empty());

                when(roleRepositoryPort.findByName(RoleName.LEARNER))
                                .thenReturn(Optional.of(role));

                when(userRepositoryPort.createNew(any(User.class)))
                                .thenReturn(newUser);

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
                LoginResult result = authUseCase.googleLogin(command);

                // Assert (Then)
                assertNotNull(result);
                assertEquals(accessToken, result.accessToken());
                assertEquals(refreshToken, result.refreshToken());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userRepositoryPort, times(1))
                                .findByEmail("user@example.com");

                verify(roleRepositoryPort, times(1))
                                .findByName(RoleName.LEARNER);

                verify(userRepositoryPort, times(1))
                                .createNew(argThat(user -> user.getFullName().equals("John Doe") &&
                                                user.getAvatarUrl().equals("http://image.png") &&
                                                user.getProviderId().equals("google_sub123") &&
                                                user.getStatus().equals(UserStatus.ACTIVE) &&
                                                user.getRoleIds().contains(100L)));

                verify(userSessionServicePort, times(1))
                                .revokeAllSessionsByUserId(2L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

                verify(userSessionRepositoryPort, times(1))
                                .save(any(UserSession.class));

                verify(userSessionEventPublisherPort, times(1))
                                .publishForceLogoutEvent(any(ForceLogoutCommand.class));

                verifyNoMoreInteractions(
                                userRepositoryPort,
                                roleRepositoryPort,
                                tokenServicePort,
                                encoderPort,
                                userSessionRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
                verifyNoInteractions(userResultMapper);
        }

        @Test
        void UTCID04_Should_ThrowException_When_UserInactive() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                User user = User.builder()
                                .id(1L)
                                .providerId("google_sub123")
                                .status(UserStatus.UNACTIVE)
                                .build();

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.of(user));

                // Act (When) & Assert (Then)
                ApplicationException exception = assertThrows(
                                ApplicationException.class,
                                () -> authUseCase.googleLogin(command));

                assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
                assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verifyNoMoreInteractions(userRepositoryPort);
                verifyNoInteractions(
                                tokenServicePort,
                                encoderPort,
                                userResultMapper,
                                userSessionRepositoryPort,
                                roleRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
        }

        @Test
        void UTCID05_Should_ThrowException_When_LearnerRoleNotFound() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.empty());

                when(userRepositoryPort.findByEmail("user@example.com"))
                                .thenReturn(Optional.empty());

                when(roleRepositoryPort.findByName(RoleName.LEARNER))
                                .thenReturn(Optional.empty());

                // Act (When) & Assert (Then)
                ApplicationException exception = assertThrows(
                                ApplicationException.class,
                                () -> authUseCase.googleLogin(command));

                assertEquals(RoleErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
                assertEquals(RoleDetailMessageKey.ROLE_ROLE_NAME_NOT_FOUND, exception.getMessage());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userRepositoryPort, times(1))
                                .findByEmail("user@example.com");

                verify(roleRepositoryPort, times(1))
                                .findByName(RoleName.LEARNER);

                verifyNoMoreInteractions(userRepositoryPort, roleRepositoryPort);
                verifyNoInteractions(
                                tokenServicePort,
                                encoderPort,
                                userResultMapper,
                                userSessionRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
        }

        @Test
        void UTCID06_Should_ThrowException_When_EmailMissing() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email(null)
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId("device-1")
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.empty());

                // userRepositoryPort.findByEmail(null) might return empty or throw
                when(userRepositoryPort.findByEmail(null))
                                .thenReturn(Optional.empty());

                Role role = Role.builder()
                                .id(100L)
                                .roleName(RoleName.LEARNER)
                                .build();

                when(roleRepositoryPort.findByName(RoleName.LEARNER))
                                .thenReturn(Optional.of(role));

                // Act (When) & Assert (Then)
                DomainException exception = assertThrows(
                                DomainException.class,
                                () -> authUseCase.googleLogin(command));

                assertEquals(UserDomainErrorCode.USER_EMAIL_NOT_VALID, exception.getErrorCode());
                assertEquals(UserDetailMessageKey.USER_EMAIL_REQUIRED, exception.getMessage());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userRepositoryPort, times(1))
                                .findByEmail(null);

                verify(roleRepositoryPort, times(1))
                                .findByName(RoleName.LEARNER);

                verifyNoMoreInteractions(userRepositoryPort, roleRepositoryPort);
                verifyNoInteractions(
                                tokenServicePort,
                                encoderPort,
                                userResultMapper,
                                userSessionRepositoryPort,
                                userSessionServicePort,
                                userSessionEventPublisherPort);
        }

        @Test
        void UTCID07_Should_ThrowException_When_DeviceIdNullOrEmpty() {
                // Arrange (Given)
                GoogleLoginCommand command = GoogleLoginCommand.builder()
                                .sub("sub123")
                                .email("user@example.com")
                                .fullName("John Doe")
                                .pictureUrl("http://image.png")
                                .deviceId(null)
                                .userAgent("Mozilla")
                                .ipAddress("127.0.0.1")
                                .build();

                User user = User.builder()
                                .id(1L)
                                .providerId("google_sub123")
                                .status(UserStatus.ACTIVE)
                                .build();

                Instant now = Instant.now();
                TokenResult refreshToken = new TokenResult(
                                "refresh_cookie",
                                "refresh_token",
                                "refresh-value",
                                now.plusSeconds(3600),
                                3600L);

                mockTransaction();

                when(userRepositoryPort.findByProviderId("google_sub123"))
                                .thenReturn(Optional.of(user));

                when(tokenServicePort.generateRefreshToken(any(Instant.class)))
                                .thenReturn(refreshToken);

                when(encoderPort.hashRefreshToken("refresh-value"))
                                .thenReturn("hashed-refresh-value");

                when(tokenServicePort.getAccessTokenExpiry(any(Instant.class)))
                                .thenReturn(now.plusSeconds(600));

                // Act (When) & Assert (Then)
                DomainException exception = assertThrows(
                                DomainException.class,
                                () -> authUseCase.googleLogin(command));

                assertEquals(UserSessionDomainErrorCode.USER_SESSION_DEVICE_ID_NOT_VALID, exception.getErrorCode());
                assertEquals(UserSessionDetailMessageKey.USER_SESSION_DEVICE_ID_BLANK, exception.getMessage());

                verify(userRepositoryPort, times(1))
                                .findByProviderId("google_sub123");

                verify(userSessionServicePort, times(1))
                                .revokeAllSessionsByUserId(1L, SessionRevokedReason.LOGIN_ON_OTHER_DEVICE);

                verify(userSessionRepositoryPort, never())
                                .save(any(UserSession.class));

                verifyNoMoreInteractions(
                                userRepositoryPort,
                                tokenServicePort,
                                encoderPort,
                                userSessionServicePort);
                verifyNoInteractions(
                                userResultMapper,
                                roleRepositoryPort,
                                userSessionRepositoryPort,
                                userSessionEventPublisherPort);
        }
}
