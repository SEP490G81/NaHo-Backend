package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.RoleEntity;
import org.naho.user.model.Role;
import org.naho.user.port.repository.RoleRepository;
import org.naho.user.repository.RoleJpaRepository;
import org.naho.user.type.RoleName;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        return roleJpaRepository.findByRoleName(roleName)
                .map(this::toDomain);
    }
    private Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .build();
    }
}
