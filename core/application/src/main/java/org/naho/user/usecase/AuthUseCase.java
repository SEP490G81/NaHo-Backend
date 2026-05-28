package org.naho.user.usecase;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.command.LogoutCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.out.*;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.result.UserResult;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;
import java.util.List;

public class AuthUseCase implements AuthPort {
    private final UserRepositoryPort userRepositoryPort;
    private final EncoderPort encoderPort;
    private final TokenServicePort tokenServicePort;
    private final UserResultMapper userResultMapper;
    private final UserSessionRepositoryPort userSessionRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final TransactionPort transactionPort;

    public AuthUseCase(
            UserRepositoryPort userRepositoryPort,
            EncoderPort encoderPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserSessionRepositoryPort userSessionRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            TransactionPort transactionPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.encoderPort = encoderPort;
        this.tokenServicePort = tokenServicePort;
        this.userResultMapper = userResultMapper;
        this.userSessionRepositoryPort = userSessionRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        return transactionPort.execute(() -> doCredentialsLogin(command));
    }

    private LoginResult doCredentialsLogin(CredentialsLoginCommand command) {
        User user = userRepositoryPort.findByUsernameOrEmail(command.usernameOrEmail())
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_LOGIN_FAILED,
                        UserApplicationMessageKey.USER_WRONG_LOGIN_INFO
                ));

        if (!encoderPort.matches(command.rawPassword(), user.getHashPassword())) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_LOGIN_FAILED,
                    UserApplicationMessageKey.USER_WRONG_LOGIN_INFO
            );
        }
        if (!user.isActive()) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_LOGIN_FAILED,
                    UserApplicationMessageKey.USER_ACCOUNT_NOT_ACTIVE
            );
        }

        if (command.deviceId() != null && !command.deviceId().isBlank()) {
            userSessionRepositoryPort.revokeActiveSessionsByUserIdAndDeviceId(
                    user.getId(),
                    command.deviceId(),
                    Instant.now(),
                    SessionRevokedReason.LOGIN_AGAIN
            );
        }

        TokenResult refreshToken = tokenServicePort.generateRefreshToken();

        String hashRefreshToken = encoderPort.hash(refreshToken.value());

        UserSession userSession = UserSession.builder()
                .userId(user.getId())
                .hashRefreshToken(hashRefreshToken)
                .deviceId(command.deviceId())
                .deviceName(command.deviceName())
                .deviceType(command.deviceType())
                .userAgent(command.userAgent())
                .ipAddress(command.ipAddress())
                .issuedAt(Instant.now())
                .expiresAt(refreshToken.expiresAt())
                .lastUsedAt(Instant.now())
                .build();

        UserSession savedUserSession = userSessionRepositoryPort.save(userSession);

        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(user.getId());
        String avatarFileUrl = fileRepositoryPort.findFileUrlById(user.getAvatarFileId());

        TokenResult accessToken = tokenServicePort.generateAccessToken(user, roleNames, savedUserSession);

        UserResult userResult = userResultMapper.domainToResult(user, roleNames, avatarFileUrl);

        return new LoginResult(
                userResult,
                accessToken,
                refreshToken
        );
    }

    @Override
    public void logout(LogoutCommand command) {
        if (command == null || command.userId() == null || command.userSessionId() == null) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_UNAUTHORIZED,
                    UserApplicationMessageKey.USER_UNAUTHORIZED_TITLE
            );
        }
        userSessionRepositoryPort.revokeActiveSessionsByUserIdAndUserSessionId(
                command.userId(),
                command.userSessionId(),
                Instant.now(),
                SessionRevokedReason.USER_LOGOUT
        );
    }

}
