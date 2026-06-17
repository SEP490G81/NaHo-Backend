package org.naho.user.usecase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.constant.TokenType;
import org.naho.user.helper.AuthUseCaseHelper;
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
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.exception.UserDomainErrorCode;
import org.naho.i18n.message.user.RoleDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.valueobject.Email;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private UserSessionRepositoryPort userSessionRepositoryPort;

    @Mock
    private TokenServicePort tokenServicePort;

    @Mock
    private AuthUseCaseHelper authUseCaseHelper;

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private UserResultMapper userResultMapper;

    @InjectMocks
    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        when(transactionPort.execute(any()))
                .thenAnswer(invocation -> {
                    Supplier<?> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
    }

    @Test
    void UTCID01_Should_Login_Successfully() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-1")
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        User user = User.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .build();

        TokenResult refreshToken = new TokenResult(
                TokenType.REFRESH_TOKEN_NAME,
                "token-value",
                Instant.now().plusSeconds(3600),
                3600L
        );

        UserSession savedUserSession = mock(UserSession.class);
        LoginResult expectedLoginResult = mock(LoginResult.class);

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.of(user));

        when(tokenServicePort.generateRefreshToken())
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken(refreshToken.value()))
                .thenReturn("hashed-refresh-token");

        when(userSessionRepositoryPort.save(any()))
                .thenReturn(savedUserSession);

        when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                .thenReturn(expectedLoginResult);

        // Act (When)
        LoginResult result = authUseCase.googleLogin(command);

        // Assert (Then)
        assertEquals(expectedLoginResult, result);

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userSessionRepositoryPort, times(1))
                .revokeActiveSessionsByUserIdAndDeviceId(
                        eq(user.getId()),
                        eq(command.getDeviceId()),
                        any(),
                        eq(SessionRevokedReason.LOGIN_AGAIN)
                );

        verify(tokenServicePort, times(1))
                .generateRefreshToken();

        verify(encoderPort, times(1))
                .hashRefreshToken(refreshToken.value());

        verify(userSessionRepositoryPort, times(1))
                .save(any());

        verify(authUseCaseHelper, times(1))
                .buildLoginResult(refreshToken, savedUserSession);

        verifyNoMoreInteractions(userRepositoryPort, userSessionRepositoryPort, tokenServicePort, encoderPort, authUseCaseHelper);
    }

    @Test
    void UTCID02_Should_Login_Successfully_When_DeviceIdBlank() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId(null)
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        User user = User.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .build();

        TokenResult refreshToken = new TokenResult(
                TokenType.REFRESH_TOKEN_NAME,
                "token-value",
                Instant.now().plusSeconds(3600),
                3600L
        );

        UserSession savedUserSession = mock(UserSession.class);
        LoginResult expectedLoginResult = mock(LoginResult.class);

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.of(user));

        when(tokenServicePort.generateRefreshToken())
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken(refreshToken.value()))
                .thenReturn("hashed-refresh-token");

        when(userSessionRepositoryPort.save(any()))
                .thenReturn(savedUserSession);

        when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                .thenReturn(expectedLoginResult);

        // Act (When) & Assert (Then)
        LoginResult result;
        try (MockedStatic<UserSession> mockedUserSessionClass = mockStatic(UserSession.class)) {
            UserSession.Builder mockBuilder = mock(UserSession.Builder.class, RETURNS_SELF);
            mockedUserSessionClass.when(UserSession::builder).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(savedUserSession);

            result = authUseCase.googleLogin(command);
        }

        assertEquals(expectedLoginResult, result);

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verify(tokenServicePort, times(1))
                .generateRefreshToken();

        verify(encoderPort, times(1))
                .hashRefreshToken(refreshToken.value());

        verify(userSessionRepositoryPort, times(1))
                .save(any());

        verify(authUseCaseHelper, times(1))
                .buildLoginResult(refreshToken, savedUserSession);

        verifyNoMoreInteractions(userRepositoryPort, userSessionRepositoryPort, tokenServicePort, encoderPort, authUseCaseHelper);
    }

    @Test
    void UTCID03_Should_ThrowException_When_UserInactive() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-1")
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        User user = User.builder()
                .id(1L)
                .status(UserStatus.UNACTIVE)
                .build();

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.of(user));

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verifyNoInteractions(tokenServicePort, encoderPort, userSessionRepositoryPort, authUseCaseHelper);
    }

    @Test
    void UTCID04_Should_Login_Successfully_When_UserFoundByEmail() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId("device-1")
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        User emailUser = spy(User.builder()
                .id(1L)
                .status(UserStatus.ACTIVE)
                .build());

        TokenResult refreshToken = new TokenResult(
                TokenType.REFRESH_TOKEN_NAME,
                "token-value",
                Instant.now().plusSeconds(3600),
                3600L
        );

        UserSession savedUserSession = mock(UserSession.class);
        LoginResult expectedLoginResult = mock(LoginResult.class);

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.empty());

        when(userRepositoryPort.findByEmail("user@example.com"))
                .thenReturn(Optional.of(emailUser));

        when(userRepositoryPort.save(emailUser))
                .thenReturn(emailUser);

        when(tokenServicePort.generateRefreshToken())
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken(refreshToken.value()))
                .thenReturn("hashed-refresh-token");

        when(userSessionRepositoryPort.save(any()))
                .thenReturn(savedUserSession);

        when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                .thenReturn(expectedLoginResult);

        // Act (When)
        LoginResult result = authUseCase.googleLogin(command);

        // Assert (Then)
        assertEquals(expectedLoginResult, result);

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userRepositoryPort, times(1))
                .findByEmail("user@example.com");

        verify(emailUser, times(1))
                .setProviderId("google_sub123");

        verify(userRepositoryPort, times(1))
                .save(emailUser);

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verify(tokenServicePort, times(1))
                .generateRefreshToken();

        verify(encoderPort, times(1))
                .hashRefreshToken(refreshToken.value());

        verify(userSessionRepositoryPort, times(1))
                .save(any());

        verify(authUseCaseHelper, times(1))
                .buildLoginResult(refreshToken, savedUserSession);

        verifyNoMoreInteractions(userRepositoryPort, userSessionRepositoryPort, tokenServicePort, encoderPort, authUseCaseHelper);
    }

    @Test
    void UTCID05_Should_Login_Successfully_When_NewUserCreated() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId(null)
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        Role learnerRole = Role.builder()
                .id(10L)
                .roleName(RoleName.LEARNER)
                .build();

        User savedNewUser = User.builder()
                .id(5L)
                .status(UserStatus.ACTIVE)
                .build();

        TokenResult refreshToken = new TokenResult(
                TokenType.REFRESH_TOKEN_NAME,
                "token-value",
                Instant.now().plusSeconds(3600),
                3600L
        );

        UserSession savedUserSession = mock(UserSession.class);
        LoginResult expectedLoginResult = mock(LoginResult.class);

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.empty());

        when(userRepositoryPort.findByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        when(roleRepositoryPort.findByName(RoleName.LEARNER))
                .thenReturn(Optional.of(learnerRole));

        when(userRepositoryPort.save(any(User.class)))
                .thenReturn(savedNewUser);

        when(tokenServicePort.generateRefreshToken())
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken(refreshToken.value()))
                .thenReturn("hashed-refresh-token");

        when(userSessionRepositoryPort.save(any()))
                .thenReturn(savedUserSession);

        when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                .thenReturn(expectedLoginResult);

        // Act (When) & Assert (Then)
        LoginResult result;
        try (MockedStatic<UserSession> mockedUserSessionClass = mockStatic(UserSession.class)) {
            UserSession.Builder mockBuilder = mock(UserSession.Builder.class, RETURNS_SELF);
            mockedUserSessionClass.when(UserSession::builder).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(savedUserSession);

            result = authUseCase.googleLogin(command);
        }

        assertEquals(expectedLoginResult, result);

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userRepositoryPort, times(1))
                .findByEmail("user@example.com");

        verify(roleRepositoryPort, times(1))
                .findByName(RoleName.LEARNER);

        verify(userRepositoryPort, times(1))
                .save(argThat(newUser -> 
                        newUser.getEmail().getValue().equals("user@example.com") &&
                        newUser.getFullName().equals("Google User") &&
                        newUser.getAvatarUrl().equals("https://example.com/avatar.jpg") &&
                        newUser.getStatus() == UserStatus.ACTIVE &&
                        newUser.getProviderId().equals("google_sub123") &&
                        newUser.getRoleIds().contains(10L)
                ));

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verify(tokenServicePort, times(1))
                .generateRefreshToken();

        verify(encoderPort, times(1))
                .hashRefreshToken(refreshToken.value());

        verify(userSessionRepositoryPort, times(1))
                .save(any());

        verify(authUseCaseHelper, times(1))
                .buildLoginResult(refreshToken, savedUserSession);

        verifyNoMoreInteractions(userRepositoryPort, userSessionRepositoryPort, tokenServicePort, encoderPort, authUseCaseHelper, roleRepositoryPort);
    }

    @Test
    void UTCID06_Should_ThrowException_When_LearnerRoleNotFound() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email("user@example.com")
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId(null)
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.empty());

        when(userRepositoryPort.findByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        when(roleRepositoryPort.findByName(RoleName.LEARNER))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(RoleErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
        assertEquals(RoleDetailMessageKey.ROLE_ROLE_NAME_NOT_FOUND, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userRepositoryPort, times(1))
                .findByEmail("user@example.com");

        verify(roleRepositoryPort, times(1))
                .findByName(RoleName.LEARNER);

        verify(userRepositoryPort, never()).save(any(User.class));
        verifyNoInteractions(tokenServicePort, encoderPort, userSessionRepositoryPort, authUseCaseHelper);
    }

    @Test
    void UTCID07_Should_ThrowException_When_EmailNull() {
        // Arrange (Given)
        GoogleLoginCommand command = GoogleLoginCommand.builder()
                .sub("sub123")
                .email(null)
                .fullName("Google User")
                .pictureUrl("https://example.com/avatar.jpg")
                .deviceId(null)
                .userAgent("Chrome")
                .ipAddress("127.0.0.1")
                .build();

        Role learnerRole = Role.builder()
                .id(10L)
                .roleName(RoleName.LEARNER)
                .build();

        when(userRepositoryPort.findByProviderId("google_sub123"))
                .thenReturn(Optional.empty());

        when(userRepositoryPort.findByEmail(null))
                .thenReturn(Optional.empty());

        when(roleRepositoryPort.findByName(RoleName.LEARNER))
                .thenReturn(Optional.of(learnerRole));

        // Act (When) & Assert (Then)
        DomainException exception = assertThrows(
                DomainException.class,
                () -> authUseCase.googleLogin(command)
        );

        assertEquals(UserDomainErrorCode.USER_EMAIL_NOT_VALID, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_EMAIL_REQUIRED, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByProviderId("google_sub123");

        verify(userRepositoryPort, times(1))
                .findByEmail(null);

        verify(roleRepositoryPort, times(1))
                .findByName(RoleName.LEARNER);

        verify(userRepositoryPort, never()).save(any(User.class));
        verifyNoInteractions(tokenServicePort, encoderPort, userSessionRepositoryPort, authUseCaseHelper);
    }
}
