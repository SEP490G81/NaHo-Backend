package org.naho.user.port.out;

public interface EncoderPort {
    boolean matches(String rawPassword, String hashPassword);

    String hash(String raw);
}
