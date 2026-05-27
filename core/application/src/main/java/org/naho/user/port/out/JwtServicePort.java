package org.naho.user.port.out;

import org.naho.user.model.User;
import org.naho.user.result.TokenResult;

public interface JwtServicePort {
    TokenResult generateAccessToken(User user);

    TokenResult generateRefreshToken(User user);
}
