package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.OAuthProviderEntity;

import java.util.List;

public interface OAuthProviderJpaRepository extends BaseJpaRepository<OAuthProviderEntity> {
    List<OAuthProviderEntity> findAllByUser_Id(Long userId);
}
