package org.naho.user.port.in;

import org.naho.user.result.RoleResult;

import java.util.List;

public interface GetRoleInputPort {
    List<RoleResult> getAllRoles();
}
