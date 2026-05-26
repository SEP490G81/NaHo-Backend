package org.naho.user.port.out;

public interface PasswordEncoderPort {
    boolean matches(String rawPassword, String hashPassword);
}
