package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.RoleEntity;
import org.naho.user.type.RoleName;

import java.util.Optional;

public interface RoleJpaRepository extends BaseJpaRepository<RoleEntity> {
    Optional<RoleEntity> findByRoleName(RoleName roleName);
}
