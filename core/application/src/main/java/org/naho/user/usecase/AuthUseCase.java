package org.naho.user.usecase;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.helper.AuthUseCaseHelper;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.in.AuthInputPort;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.result.UserResult;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;
import java.util.List;

public class AuthUseCase implements AuthInputPort {
    private final UserRepositoryPort userRepositoryPort;
    private final EncoderPort encoderPort;
    private final TokenServicePort tokenServicePort;
    private final UserResultMapper userResultMapper;
    private final UserSessionRepositoryPort userSessionRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final TransactionPort transactionPort;
    private final AuthUseCaseHelper authUseCaseHelper;

    public AuthUseCase(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            TransactionPort transactionPort,
            AuthUseCaseHelper authUseCaseHelper) {
        this.userRepositoryPort = userRepositoryPort;
        this.encoderPort = encoderPort;
        this.tokenServicePort = tokenServicePort;
        this.userResultMapper = userResultMapper;
        this.userSessionRepositoryPort = userSessionRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.transactionPort = transactionPort;
        this.authUseCaseHelper = authUseCaseHelper;
    }

    @Override
    public UserResult findUserById(Long userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));
        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(userId);
        String avatarObjectKey = fileRepositoryPort.findObjectKeyById(user.getAvatarFileId());
        return userResultMapper.domainToResult(user, roleNames, avatarObjectKey);
    }

    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        return transactionPort.execute(() -> doCredentialsLogin(command));
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

        if (command.deviceId() != null && !command.deviceId().isBlank()) {
            userSessionRepositoryPort.revokeActiveSessionsByUserIdAndDeviceId(
                    user.getId(),
                    command.deviceId(),
                    Instant.now(),
                    SessionRevokedReason.LOGIN_AGAIN);
        }

        TokenResult refreshToken = tokenServicePort.generateRefreshToken();

        String hashRefreshToken = encoderPort.hashRefreshToken(refreshToken.value());

        Instant now = Instant.now();
        UserSession userSession = UserSession.builder()
                .userId(user.getId())
                .hashRefreshToken(hashRefreshToken)
                .deviceId(command.deviceId())
                .deviceName(command.deviceName())
                .deviceType(command.deviceType())
                .userAgent(command.userAgent())
                .ipAddress(command.ipAddress())
                .issuedAt(now)
                .expiresAt(refreshToken.expiresAt())
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(userSession);

        return authUseCaseHelper.buildLoginResult(refreshToken, savedUserSession);
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

        if (userSession.isExpired()) {
            userSession.setRevokedAt(now);
            userSession.setRevokedReason(SessionRevokedReason.EXPIRED);
            userSessionRepositoryPort.save(userSession);

            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                    UserDetailMessageKey.USER_REFRESH_TOKEN_EXPIRED);
        }

        if (userSession.isRevoked()) {
            userSession.setRevokedReason(SessionRevokedReason.TOKEN_REUSE_DETECTED);
            userSessionRepositoryPort.save(userSession);

            throw new ApplicationException(
                    UserErrorCode.USER_INVALID_REFRESH_TOKEN,
                    UserDetailMessageKey.USER_REFRESH_TOKEN_REVOKED);
        }

        return transactionPort.execute(() -> doRotateToken(userSession, now));
    }

    private LoginResult doRotateToken(UserSession userSession, Instant now) {
        // revoke old refresh token
        userSession.setRevokedReason(SessionRevokedReason.ROTATED);
        userSession.setRevokedAt(now);
        userSession.setLastUsedAt(now);
        userSessionRepositoryPort.save(userSession);

        // generate new refresh token and new user session
        TokenResult newRefreshToken = tokenServicePort.generateRefreshToken();

        String newHashRefreshToken = encoderPort.hashRefreshToken(newRefreshToken.value());

        UserSession newUserSession = UserSession.builder()
                .userId(userSession.getUserId())
                .hashRefreshToken(newHashRefreshToken)
                .deviceId(userSession.getDeviceId())
                .deviceName(userSession.getDeviceName())
                .deviceType(userSession.getDeviceType())
                .userAgent(userSession.getUserAgent())
                .ipAddress(userSession.getIpAddress())
                .issuedAt(now)
                .expiresAt(newRefreshToken.expiresAt())
                .lastUsedAt(now)
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(newUserSession);

        return authUseCaseHelper.buildLoginResult(newRefreshToken, savedUserSession);
    }
}
