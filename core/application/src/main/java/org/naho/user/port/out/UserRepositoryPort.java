package org.naho.user.port.out;

import org.naho.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    List<User> getListUser();
}
