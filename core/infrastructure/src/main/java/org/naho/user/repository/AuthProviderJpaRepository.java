package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.AuthProviderEntity;

import java.util.List;

public interface AuthProviderJpaRepository extends BaseJpaRepository<AuthProviderEntity> {
    List<AuthProviderEntity> findAllByUser_Id(Long userId);
}
