package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserJpaRepository extends BaseJpaRepository<UserEntity> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}
