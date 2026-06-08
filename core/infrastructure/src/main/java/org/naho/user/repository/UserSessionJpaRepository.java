package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserSessionEntity;

import java.util.Optional;

public interface UserSessionJpaRepository extends BaseJpaRepository<UserSessionEntity> {
    Optional<UserSessionEntity> findByHashRefreshToken(String hashRefreshToken);
}
