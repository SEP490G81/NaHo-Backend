package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserJpaRepository extends BaseJpaRepository<UserEntity> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
