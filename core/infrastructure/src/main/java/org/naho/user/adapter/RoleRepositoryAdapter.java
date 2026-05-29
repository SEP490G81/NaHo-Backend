package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.entity.RoleEntity;
import org.naho.user.mapper.RoleEntityMapper;
import org.naho.user.model.Role;
import org.naho.user.mybatis.RoleQueryMapper;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleQueryMapper roleQueryMapper;
    private final RoleJpaRepository roleJpaRepository;
    private final RoleEntityMapper roleEntityMapper;

    @Override
    public List<String> findRoleNamesByUserId(Long userId) {
        return roleQueryMapper.findRoleNamesByUserId(userId);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(roleEntityMapper::entityToDomain)
                .toList();
    }
}
