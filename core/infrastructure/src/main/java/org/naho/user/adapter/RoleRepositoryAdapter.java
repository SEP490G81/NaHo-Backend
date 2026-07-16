package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.RoleEntity;
import org.naho.user.mapper.RoleEntityMapper;
import org.naho.user.model.Role;
import org.naho.user.mybatis.RoleQueryMapper;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.repository.RoleJpaRepository;
import org.naho.user.type.RoleName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleQueryMapper roleQueryMapper;
    private final RoleJpaRepository roleJpaRepository;
    private final RoleEntityMapper roleEntityMapper;

    @Override
    public List<String> findRoleNamesByUserId(Long userId) {
        if (userId == null) return List.of();
        return roleQueryMapper.findRoleNamesByUserId(userId);
    }

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        RoleEntity roleEntity = roleQueryMapper.findByName(roleName);
        return Optional.ofNullable(roleEntityMapper.entityToDomain(roleEntity));
    }

    @Override
    public List<Role> findAllByUserId(Long userId) {
        List<RoleEntity> roleEntityList = roleQueryMapper.findAllByUserId(userId);
        return roleEntityList.stream()
                .map(roleEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(roleEntityMapper::entityToDomain)
                .toList();
    }
}
