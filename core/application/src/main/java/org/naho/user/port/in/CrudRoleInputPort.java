package org.naho.user.port.in;

import org.naho.user.result.RoleResult;

import java.util.List;

public interface CrudRoleInputPort {
    List<RoleResult> getAllRoles();

    RoleResult findRoleById(Long roleId);

    RoleResult findRoleByUserId(Long userId);

    List<RoleResult> findAllByUserId(Long userId);
}
