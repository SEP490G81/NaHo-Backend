package org.naho.user.usecase;

import org.naho.user.port.in.GetRoleInputPort;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.result.RoleResult;

import java.util.List;

public class GetRoleUseCase implements GetRoleInputPort {
    private final RoleRepositoryPort roleRepositoryPort;

    public GetRoleUseCase(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public List<RoleResult> getAllRoles() {
        return roleRepositoryPort.findAll().stream()
                .map(role -> new RoleResult(role.getId(), role.getRoleName(), role.getDescription()))
                .toList();
    }
}
