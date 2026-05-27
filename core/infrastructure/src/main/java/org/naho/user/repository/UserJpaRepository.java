package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserEntity;

public interface UserJpaRepository extends BaseJpaRepository<UserEntity> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
