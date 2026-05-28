package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.mybatis.RoleQueryMapper;
import org.naho.user.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleQueryMapper roleQueryMapper;

    @Override
    public List<String> findRoleNamesByUserId(Long userId) {
        return roleQueryMapper.findRoleNamesByUserId(userId);
    }
}
