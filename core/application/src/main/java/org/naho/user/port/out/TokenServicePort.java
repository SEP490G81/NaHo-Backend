package org.naho.user.port.out;

import org.naho.user.model.UserSession;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.TokenResult;

import java.time.Instant;

public interface TokenServicePort {
    TokenResult generateAccessToken(UserSession userSession);

    default TokenResult generateAccessToken(UserSession userSession, boolean isAdmin) {
        return generateAccessToken(userSession);
    }

    TokenResult generateRefreshToken(Instant issuedAt);

    default TokenResult generateRefreshToken(Instant issuedAt, boolean isAdmin) {
        return generateRefreshToken(issuedAt);
    }

    AccessTokenPayload verifyAccessToken(String accessToken);

    Instant getAccessTokenExpiry(Instant issuedAt);
}
