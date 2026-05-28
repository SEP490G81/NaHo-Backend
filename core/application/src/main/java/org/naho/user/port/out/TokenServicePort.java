package org.naho.user.port.out;

import org.naho.user.model.User;
import org.naho.user.model.UserSession;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.TokenResult;

import java.util.List;

public interface TokenServicePort {
    TokenResult generateAccessToken(User user, List<String> roleNames, UserSession userSession);

    TokenResult generateRefreshToken();

    AccessTokenPayload verifyAccessToken(String accessToken);
}
