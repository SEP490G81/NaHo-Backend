package org.naho.user.mapper;

import org.naho.user.model.Role;
import org.naho.user.result.RoleResult;

public class RoleResultMapper {
    public RoleResult domainToResult(Role domain) {
        return new RoleResult(
                domain.getId(),
                domain.getRoleName(),
                domain.getDescription()
        );
    }
}
