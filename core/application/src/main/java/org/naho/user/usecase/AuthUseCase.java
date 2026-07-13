package org.naho.user.usecase;

import org.naho.i18n.message.user.RoleDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.ForceLogoutCommand;
import org.naho.user.command.GoogleLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.exception.RoleErrorCode;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.Role;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.type.RoleName;
import org.naho.user.type.SessionRevokedReason;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Email;

import java.time.Instant;
import java.util.List;

public class AuthUseCase implements AuthInputPort {
    private static final String PROVIDER_GOOGLE = "google_";

    private final UserRepositoryPort userRepositoryPort;
    private final EncoderPort encoderPort;
    private final TokenServicePort tokenServicePort;
    private final UserSessionRepositoryPort userSessionRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final TransactionPort transactionPort;
    private final UserSessionServicePort userSessionServicePort;
    private final UserSessionEventPublisherPort userSessionEventPublisherPort;

    public AuthUseCase(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            TransactionPort transactionPort,
            UserSessionServicePort userSessionServicePort,
            UserSessionEventPublisherPort userSessionEventPublisherPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.encoderPort = encoderPort;
        this.tokenServicePort = tokenServicePort;
        this.userSessionRepositoryPort = userSessionRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.transactionPort = transactionPort;
        this.userSessionServicePort = userSessionServicePort;
        this.userSessionEventPublisherPort = userSessionEventPublisherPort;
    }

    @Override
    public void logoutAllSessions(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserDetailMessageKey.USER_UNAUTHORIZED
            );
        }
        userSessionRepositoryPort.revokeAllActiveSessionsByUserId(
                userId,
                Instant.now(),
                SessionRevokedReason.USER_LOGOUT_ALL
        );
    }

    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        return transactionPort.execute(() -> doCredentialsLogin(command));
    }

    @Override
    public LoginResult googleLogin(GoogleLoginCommand command) {
        return transactionPort.execute(() -> doGoogleLogin(command));
    }

    private LoginResult doGoogleLogin(GoogleLoginCommand command) {
        String providerId = PROVIDER_GOOGLE + command.getSub();
        User currentUser = userRepositoryPort.findByProviderId(providerId)
                .orElse(null);

        if (currentUser == null) {
            User emailUser = userRepositoryPort.findByEmail(command.getEmail())
                    .orElse(null);
            // if user is not found by providerId and email, create new
            if (emailUser == null) {
                Role learnerRole = roleRepositoryPort.findByName(RoleName.LEARNER)
                        .orElseThrow(() -> new ApplicationException(
                                RoleErrorCode.ROLE_NOT_FOUND,
                                RoleDetailMessageKey.ROLE_ROLE_NAME_NOT_FOUND,
                                RoleName.LEARNER.name()
                        ));

                User newUser = User.builder()
                        .email(Email.of(command.getEmail()))
                        .fullName(command.getFullName())
                        .avatarUrl(command.getPictureUrl())
                        .status(UserStatus.ACTIVE)
                        .roleIds(List.of(learnerRole.getId()))
                        .providerId(providerId)
                        .build();

                currentUser = userRepositoryPort.createNew(newUser);
            } else {
                // if user is found by email, update providerId
                emailUser.setProviderId(providerId);
                currentUser = userRepositoryPort.save(emailUser);
            }
        }

        if (!currentUser.isActive()) {
            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE);
        }

        userSessionServicePort.revokeAllSessionsByUserId(
                currentUser.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        );

        Instant now = Instant.now();

        TokenResult refreshToken = tokenServicePort.generateRefreshToken(now);

        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken.value());

        UserSession userSession = UserSession.builder()
                .userId(currentUser.getId())
                .hashRefreshToken(hashRefreshToken)
                .deviceId(command.getDeviceId())
                .userAgent(command.getUserAgent())
                .ipAddress(command.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(tokenServicePort.getAccessTokenExpiry(now))
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(userSession);

        TokenResult accessToken = tokenServicePort.generateAccessToken(savedUserSession);

        userSessionEventPublisherPort.publishForceLogoutEvent(new ForceLogoutCommand(
                savedUserSession.getUserId(),
                savedUserSession.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        ));

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }

    private LoginResult doCredentialsLogin(CredentialsLoginCommand command) {
        User user = userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_LOGIN_FAILED,
                        UserDetailMessageKey.USER_WRONG_LOGIN_INFO));

        if (!encoderPort.matches(command.rawPassword(), user.getHashPassword())) {
            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_WRONG_LOGIN_INFO);
        }

        if (!user.isActive()) {
            throw new ApplicationException(
                    UserErrorCode.USER_LOGIN_FAILED,
                    UserDetailMessageKey.USER_ACCOUNT_NOT_ACTIVE);
        }

        userSessionServicePort.revokeAllSessionsByUserId(
                user.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        );

        Instant now = Instant.now();

        TokenResult refreshToken = tokenServicePort.generateRefreshToken(now);

        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken.value());

        UserSession userSession = UserSession.builder()
                .userId(user.getId())
                .hashRefreshToken(hashRefreshToken)
                .deviceId(command.deviceId())
                .userAgent(command.userAgent())
                .ipAddress(command.ipAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .accessTokenExpiresAt(tokenServicePort.getAccessTokenExpiry(now))
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(userSession);

        TokenResult accessToken = tokenServicePort.generateAccessToken(savedUserSession);

        userSessionEventPublisherPort.publishForceLogoutEvent(new ForceLogoutCommand(
                savedUserSession.getUserId(),
                savedUserSession.getId(),
                SessionRevokedReason.LOGIN_ON_OTHER_DEVICE
        ));

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }

    @Override
    public void logout(LogoutCommand command) {
        if (command == null || command.userId() == null || command.userSessionId() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_UNAUTHORIZED,
                    UserTitleMessageKey.USER_UNAUTHORIZED_TITLE);
        }
        userSessionRepositoryPort.revokeActiveSessionsByUserIdAndUserSessionId(
                command.userId(),
                command.userSessionId(),
                Instant.now(),
                SessionRevokedReason.USER_LOGOUT
        );
    }

    @Override
    public LoginResult rotateToken(String refreshToken) {
        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken);
        UserSession userSession = userSessionRepositoryPort.findByHashRefreshToken(hashRefreshToken);
        Instant now = Instant.now();

        userSessionRepositoryPort.verifyUserSession(userSession, now);

        return transactionPort.execute(() -> doRotateToken(userSession, now));
    }

    private LoginResult doRotateToken(UserSession userSession, Instant now) {
        // revoke old refresh token
        userSession.setRevokedReason(SessionRevokedReason.ROTATED);
        userSession.setRevokedAt(now);
        userSession.setLastUsedAt(now);
        userSessionRepositoryPort.save(userSession);

        // generate new refresh token and new user session
        TokenResult newRefreshToken = tokenServicePort.generateRefreshToken(now);

        String newHashRefreshToken = encoderPort.hashRefreshToken(newRefreshToken.value());

        UserSession newUserSession = UserSession.builder()
                .userId(userSession.getUserId())
                .hashRefreshToken(newHashRefreshToken)
                .deviceId(userSession.getDeviceId())
                .userAgent(userSession.getUserAgent())
                .ipAddress(userSession.getIpAddress())
                .issuedAt(now)
                .refreshTokenExpiresAt(newRefreshToken.expiresAt())
                .accessTokenExpiresAt(tokenServicePort.getAccessTokenExpiry(now))
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(newUserSession);

        TokenResult newAccessToken = tokenServicePort.generateAccessToken(savedUserSession);

        return new LoginResult(
                newAccessToken,
                newRefreshToken
        );
    }
}
