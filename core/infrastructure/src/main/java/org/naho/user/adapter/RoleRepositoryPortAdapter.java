package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.model.Role;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleRepositoryPortAdapter implements RoleRepositoryPort {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(entity -> Role.builder()
                        .id(entity.getId())
                        .roleName(entity.getRoleName())
                        .description(entity.getDescription())
                        .build())
                .toList();
    }
}
