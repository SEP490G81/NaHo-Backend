package org.naho.user.port.out;

import org.naho.user.model.UserSession;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.TokenResult;

import java.time.Instant;

public interface TokenServicePort {
    TokenResult generateAccessToken(UserSession userSession);

    TokenResult generateRefreshToken(Instant issuedAt);

    AccessTokenPayload verifyAccessToken(String accessToken);

    Instant getAccessTokenExpiry(Instant issuedAt);
}
