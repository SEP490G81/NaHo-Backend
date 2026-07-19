package org.naho.user.usecase;

import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.RoleResultMapper;
import org.naho.user.model.Role;
import org.naho.user.port.in.CrudRoleInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.RoleResult;

import java.util.List;

public class CrudRoleUseCase implements CrudRoleInputPort {
    private final RoleRepositoryPort roleRepositoryPort;
    private final RoleResultMapper roleResultMapper;

    public CrudRoleUseCase(
            RoleRepositoryPort roleRepositoryPort,
            RoleResultMapper roleResultMapper
    ) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.roleResultMapper = roleResultMapper;
    }

    @Override
    public List<RoleResult> getAllRoles() {
        return roleRepositoryPort.findAll().stream()
                .map(roleResultMapper::domainToResult)
                .toList();
    }

    @Override
    public List<RoleResult> findAllByUserId(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<Role> roles = roleRepositoryPort.findAllByUserId(userId);
        return roles.stream()
                .map(roleResultMapper::domainToResult)
                .toList();
    }
}
