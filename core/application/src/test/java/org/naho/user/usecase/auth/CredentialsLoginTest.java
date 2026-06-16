package org.naho.user.usecase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.constant.TokenType;
import org.naho.user.helper.AuthUseCaseHelper;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.EncoderPort;
import org.naho.user.port.out.TokenServicePort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.port.out.UserSessionRepositoryPort;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.usecase.AuthUseCase;
import org.mockito.MockedStatic;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.i18n.message.user.UserDetailMessageKey;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// annotation sử dụng để khi chạy test class này,
// sẽ khởi tạo và quản lí các Mockito mock
// nếu không thì nó sẽ bị null (ví dụ: private UserRepositoryPort userRepositoryPort)
@ExtendWith(MockitoExtension.class)
class CredentialsLoginTest {

    // tạo ra 1 object giả
    // thay vì: UserRepositoryPort repository = new UserRepositoryAdapter()
    // thì mockito sẽ tạo: UserRepositoryPort repository = mock(UserRepositoryPort.class)
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

    // mockito sẽ tạo AuthUseCase authUseCase
    // và inject toàn bộ mock vào constructor
    // ví dụ: authUseCase =
    //        new AuthUseCase(
    //                userRepositoryPort,
    //                encoderPort,
    //                tokenServicePort,
    //                ...
    //        )
    @InjectMocks
    private AuthUseCase authUseCase;

    // chạy trước mỗi test method
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
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                "device-1",
                "Chrome",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed-password")
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

        when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                .thenReturn(true);

        when(tokenServicePort.generateRefreshToken())
                .thenReturn(refreshToken);

        when(encoderPort.hashRefreshToken(refreshToken.value()))
                .thenReturn("hashed-refresh-token");

        when(userSessionRepositoryPort.save(any()))
                .thenReturn(savedUserSession);

        when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                .thenReturn(expectedLoginResult);

        // Act (When)
        LoginResult result = authUseCase.credentialsLogin(command);

        // Assert (Then)
        assertEquals(expectedLoginResult, result);

        // verify() dùng để kiểm tra một method có được gọi hay không
        // và được gọi bao nhiêu lần, với tham số như nào,...
        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail(command.usernameOrEmail());

        verify(encoderPort, times(1))
                .matches(command.rawPassword(), user.getHashPassword());

        // eq() dùng để kiểm tra tham số truyền vào phải bằng giá trị mong muốn
        // tại sao không viết thẳng giá trị?
        // ví dụ:
        // verify(encoderPort, times(1)).hashRefreshToken(refreshToken.value())
        // vẫn chạy được
        // nhưng khi dùng thêm các matcher khác thì Mockito yêu cầu phải:
        // hoặc tất cả tham số là matcher hoặc không tham số nào là matcher
        // khi không dùng matcher thì giá trị truyền vào mặc định được so sánh bằng eq()
        verify(userSessionRepositoryPort, times(1))
                .revokeActiveSessionsByUserIdAndDeviceId(
                        eq(user.getId()),
                        eq(command.deviceId()),
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
    }

    @Test
    void UTCID02_Should_ThrowException_When_PasswordIncorrect() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "wrong-password",
                "device-1",
                "Chrome",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed-password")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                .thenReturn(false);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail(command.usernameOrEmail());

        verify(encoderPort, times(1))
                .matches(command.rawPassword(), user.getHashPassword());

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verify(tokenServicePort, never())
                .generateRefreshToken();

        verify(userSessionRepositoryPort, never())
                .save(any());
    }

    @Test
    void UTCID03_Should_ThrowException_When_UserNotFound() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "unknown",
                "Admin@123",
                null,
                "Chrome",
                "127.0.0.1"
        );

        when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.empty());

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail(command.usernameOrEmail());

        verifyNoInteractions(encoderPort);
        verifyNoInteractions(userSessionRepositoryPort);
        verifyNoInteractions(tokenServicePort);
    }

    @Test
    void UTCID04_Should_ThrowException_When_UserInactive() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                "device-1",
                "Chrome",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed-password")
                .status(UserStatus.UNACTIVE)
                .build();

        when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                .thenReturn(true);

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> authUseCase.credentialsLogin(command)
        );

        assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
        assertEquals(UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE, exception.getMessage());

        verify(userRepositoryPort, times(1))
                .findByUsernameOrEmail(command.usernameOrEmail());

        verify(encoderPort, times(1))
                .matches(command.rawPassword(), user.getHashPassword());

        verify(userSessionRepositoryPort, never())
                .revokeActiveSessionsByUserIdAndDeviceId(any(), any(), any(), any());

        verifyNoInteractions(tokenServicePort);
    }

    @Test
    void UTCID05_Should_Login_Successfully() {
        // Arrange (Given)
        CredentialsLoginCommand command = new CredentialsLoginCommand(
                "admin",
                "Admin@123",
                "device-1",
                "Chrome",
                "127.0.0.1"
        );

        User user = User.builder()
                .id(1L)
                .hashPassword("hashed-password")
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
    
            when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                    .thenReturn(Optional.of(user));
    
            when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                    .thenReturn(true);
    
            when(tokenServicePort.generateRefreshToken())
                    .thenReturn(refreshToken);
    
            when(encoderPort.hashRefreshToken(refreshToken.value()))
                    .thenReturn("hashed-refresh-token");
    
            when(userSessionRepositoryPort.save(any()))
                    .thenReturn(savedUserSession);
    
            when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                    .thenReturn(expectedLoginResult);
    
            // Act (When)
            LoginResult result = authUseCase.credentialsLogin(command);
    
            // Assert (Then)
            assertEquals(expectedLoginResult, result);
    
            verify(userRepositoryPort, times(1))
                    .findByUsernameOrEmail(command.usernameOrEmail());
    
            verify(encoderPort, times(1))
                    .matches(command.rawPassword(), user.getHashPassword());
    
            verify(userSessionRepositoryPort, times(1))
                    .revokeActiveSessionsByUserIdAndDeviceId(
                            eq(user.getId()),
                            eq(command.deviceId()),
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
        }
    
        @Test
        void UTCID06_Should_Login_Successfully_When_DeviceIdNull() {
            // Arrange (Given)
            CredentialsLoginCommand command = new CredentialsLoginCommand(
                    "admin",
                    "Admin@123",
                    null,
                    "Chrome",
                    "127.0.0.1"
            );
    
            User user = User.builder()
                    .id(1L)
                    .hashPassword("hashed-password")
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
    
            when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                    .thenReturn(Optional.of(user));
    
            when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                    .thenReturn(true);
    
            when(tokenServicePort.generateRefreshToken())
                    .thenReturn(refreshToken);
    
            when(encoderPort.hashRefreshToken(refreshToken.value()))
                    .thenReturn("hashed-refresh-token");
    
            when(userSessionRepositoryPort.save(any()))
                    .thenReturn(savedUserSession);
    
            when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                    .thenReturn(expectedLoginResult);
    
            // Act (When)
            LoginResult result;
            try (MockedStatic<UserSession> mockedUserSessionClass = mockStatic(UserSession.class)) {
                UserSession.Builder mockBuilder = mock(UserSession.Builder.class, RETURNS_SELF);
                mockedUserSessionClass.when(UserSession::builder).thenReturn(mockBuilder);
                when(mockBuilder.build()).thenReturn(savedUserSession);
    
                result = authUseCase.credentialsLogin(command);
            }
    
            // Assert (Then)
            assertEquals(expectedLoginResult, result);
    
            verify(userRepositoryPort, times(1))
                    .findByUsernameOrEmail(command.usernameOrEmail());
    
            verify(encoderPort, times(1))
                    .matches(command.rawPassword(), user.getHashPassword());
    
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
        }
    
        @Test
        void UTCID07_Should_Login_Successfully() {
            // Arrange (Given)
            CredentialsLoginCommand command = new CredentialsLoginCommand(
                    "admin",
                    "Admin@123",
                    "device-1",
                    "Chrome",
                    "127.0.0.1"
            );
    
            User user = User.builder()
                    .id(1L)
                    .hashPassword("hashed-password")
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
    
            when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                    .thenReturn(Optional.of(user));
    
            when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                    .thenReturn(true);
    
            when(tokenServicePort.generateRefreshToken())
                    .thenReturn(refreshToken);
    
            when(encoderPort.hashRefreshToken(refreshToken.value()))
                    .thenReturn("hashed-refresh-token");
    
            when(userSessionRepositoryPort.save(any()))
                    .thenReturn(savedUserSession);
    
            when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                    .thenReturn(expectedLoginResult);
    
            // Act (When)
            LoginResult result = authUseCase.credentialsLogin(command);
    
            // Assert (Then)
            assertEquals(expectedLoginResult, result);
    
            verify(userRepositoryPort, times(1))
                    .findByUsernameOrEmail(command.usernameOrEmail());
    
            verify(encoderPort, times(1))
                    .matches(command.rawPassword(), user.getHashPassword());
    
            verify(userSessionRepositoryPort, times(1))
                    .revokeActiveSessionsByUserIdAndDeviceId(
                            eq(user.getId()),
                            eq(command.deviceId()),
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
        }
    
        @Test
        void UTCID08_Should_ThrowException_When_UsernameOrEmailAndRawPasswordNull() {
            // Arrange (Given)
            CredentialsLoginCommand command = new CredentialsLoginCommand(
                    null,
                    null,
                    null,
                    "Chrome",
                    "127.0.0.1"
            );
    
            when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                    .thenReturn(Optional.empty());
    
            // Act (When) & Assert (Then)
            ApplicationException exception = assertThrows(
                    ApplicationException.class,
                    () -> authUseCase.credentialsLogin(command)
            );
    
            assertEquals(UserErrorCode.USER_LOGIN_FAILED, exception.getErrorCode());
            assertEquals(UserDetailMessageKey.USER_WRONG_LOGIN_INFO, exception.getMessage());
    
            verify(userRepositoryPort, times(1))
                    .findByUsernameOrEmail(command.usernameOrEmail());
    
            verifyNoInteractions(encoderPort);
            verifyNoInteractions(userSessionRepositoryPort);
            verifyNoInteractions(tokenServicePort);
        }
    
        @Test
        void UTCID09_Should_Login_Successfully() {
            // Arrange (Given)
            CredentialsLoginCommand command = new CredentialsLoginCommand(
                    "admin",
                    "Admin@123",
                    "device-1",
                    "Chrome",
                    "127.0.0.1"
            );
    
            User user = User.builder()
                    .id(1L)
                    .hashPassword("hashed-password")
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
    
            when(userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail()))
                    .thenReturn(Optional.of(user));
    
            when(encoderPort.matches(command.rawPassword(), user.getHashPassword()))
                    .thenReturn(true);
    
            when(tokenServicePort.generateRefreshToken())
                    .thenReturn(refreshToken);
    
            when(encoderPort.hashRefreshToken(refreshToken.value()))
                    .thenReturn("hashed-refresh-token");
    
            when(userSessionRepositoryPort.save(any()))
                    .thenReturn(savedUserSession);
    
            when(authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession))
                    .thenReturn(expectedLoginResult);
    
            // Act (When)
            LoginResult result = authUseCase.credentialsLogin(command);
    
            // Assert (Then)
            assertEquals(expectedLoginResult, result);
    
            verify(userRepositoryPort, times(1))
                    .findByUsernameOrEmail(command.usernameOrEmail());
    
            verify(encoderPort, times(1))
                    .matches(command.rawPassword(), user.getHashPassword());
    
            verify(userSessionRepositoryPort, times(1))
                    .revokeActiveSessionsByUserIdAndDeviceId(
                            eq(user.getId()),
                            eq(command.deviceId()),
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
        }
}