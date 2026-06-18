package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserSessionEntity;

import java.util.List;
import java.util.Optional;

public interface UserSessionJpaRepository extends BaseJpaRepository<UserSessionEntity> {
    Optional<UserSessionEntity> findByHashRefreshToken(String hashRefreshToken);

    Optional<UserSessionEntity> findByUser_Id(Long userId);

    List<UserSessionEntity> findAllByUser_IdAndRevokedAtIsNullAndRevokedReasonIsNull(Long userId);
}
