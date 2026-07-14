package org.naho.user.port.in;

import org.naho.user.result.RoleResult;

import java.util.List;

public interface CrudRoleInputPort {
    List<RoleResult> getAllRoles();

    List<RoleResult> findAllByUserId(Long userId);
}
