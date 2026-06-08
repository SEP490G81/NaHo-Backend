package org.naho.user.port.out;

public interface EncoderPort {
    boolean matches(String rawPassword, String hashPassword);

    String hashRefreshToken(String raw);

    String hashPassword(String raw);
}
