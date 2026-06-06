package org.naho.user.helper;

import org.naho.file.port.out.FileRepositoryPort;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.UserSession;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.TokenServicePort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LoginResult;
import org.naho.user.result.TokenResult;

import java.util.List;

public class AuthUseCaseHelper {
    private final RoleRepositoryPort roleRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final TokenServicePort tokenServicePort;
    private final UserResultMapper userResultMapper;
    private final UserRepositoryPort userRepositoryPort;

    public AuthUseCaseHelper(
            RoleRepositoryPort roleRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            TokenServicePort tokenServicePort,
            UserResultMapper userResultMapper,
            UserRepositoryPort userRepositoryPort
    ) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.tokenServicePort = tokenServicePort;
        this.userResultMapper = userResultMapper;
        this.userRepositoryPort = userRepositoryPort;
    }

    public LoginResult buildLoginResult(TokenResult refreshToken, UserSession userSession) {
        Long userId = userSession.getUserId();

        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(userId);

        TokenResult accessToken = tokenServicePort.generateAccessToken(roleNames, userSession);

        return new LoginResult(
                accessToken,
                refreshToken
        );
    }
}
