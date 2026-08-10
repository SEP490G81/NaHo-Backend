package org.naho.user.dto.response;

import org.naho.user.type.RoleName;

public record RoleResponse(
        Long id,
        RoleName roleName,
        String description
) {
}
