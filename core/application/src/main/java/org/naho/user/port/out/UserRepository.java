package org.naho.user.port.out;

import org.naho.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
