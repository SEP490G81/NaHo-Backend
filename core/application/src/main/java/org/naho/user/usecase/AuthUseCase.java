package org.naho.user.usecase;

import org.naho.shared.exception.ApplicationException;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.exception.UserApplicationErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.AuthPort;
import org.naho.user.port.out.JwtServicePort;
import org.naho.user.port.out.PasswordEncoderPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;
import org.naho.user.result.UserResult;
import org.naho.user.type.UserStatus;

public class AuthUseCase implements AuthPort {
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;
    private final UserResultMapper userResultMapper;

    public AuthUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            JwtServicePort jwtServicePort,
            UserResultMapper userResultMapper
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
        this.userResultMapper = userResultMapper;
    }

    @Override
    public LoginResult credentialsLogin(CredentialsLoginCommand command) {
        User user = userRepositoryPort.findByUsername(command.username())
                .orElseThrow(() -> new ApplicationException(
                        UserApplicationErrorCode.USER_LOGIN_FAILED,
                        UserApplicationMessageKey.USER_WRONG_USERNAME_OR_PASSWORD
                ));
        if (!passwordEncoderPort.matches(command.rawPassword(), user.getHashPassword())) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_LOGIN_FAILED,
                    UserApplicationMessageKey.USER_WRONG_USERNAME_OR_PASSWORD
            );
        }
        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new ApplicationException(
                    UserApplicationErrorCode.USER_LOGIN_FAILED,
                    UserApplicationMessageKey.USER_ACCOUNT_NOT_ACTIVE
            );
        }

        TokenResult accessToken = jwtServicePort.generateAccessToken(user);
        TokenResult refreshToken = jwtServicePort.generateRefreshToken(user);
        UserResult userResult = userResultMapper.domainToResult(user);

        return new LoginResult(
                userResult,
                accessToken,
                refreshToken
        );
    }

}
