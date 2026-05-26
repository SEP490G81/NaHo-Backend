package org.naho.user.port.out;

import org.naho.user.model.User;

public interface JwtServicePort {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);
}
