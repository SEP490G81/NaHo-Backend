package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.user.mybatis.PermissionQueryMapper;
import org.naho.user.port.out.PermissionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {
    private final PermissionQueryMapper permissionQueryMapper;

    @Override
    public List<String> findAllPermissionCodeByUserId(Long userId) {
        return permissionQueryMapper.findAllPermissionCodeByUserId(userId);
    }

}
