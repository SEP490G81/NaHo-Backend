package org.naho.user.port.out;

import org.naho.user.model.UserSession;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.GoogleUserInfoResult;
import org.naho.user.result.TokenResult;

import java.util.List;

public interface TokenServicePort {
    TokenResult generateAccessToken(List<String> roleNames, UserSession userSession);

    TokenResult generateRefreshToken();

    AccessTokenPayload verifyAccessToken(String accessToken);

    GoogleUserInfoResult verifyGoogleToken(String idToken);
}
