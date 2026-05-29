package org.naho.user.port.out;

import org.naho.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findById(Long id);

    List<User> getListUser();

    void save(User user);

    List<User> findByFilters(String userNameOrMail, String role, String status, String jlptLevel);
}
