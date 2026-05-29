package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.model.Role;
import org.naho.user.mybatis.RoleQueryMapper;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.type.RoleName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleQueryMapper roleQueryMapper;

    @Override
    public List<String> findRoleNamesByUserId(Long userId) {
        return roleQueryMapper.findRoleNamesByUserId(userId);
    }

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        return roleQueryMapper.findByName(roleName);
    }
}
