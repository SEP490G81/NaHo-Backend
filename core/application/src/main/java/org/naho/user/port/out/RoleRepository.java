package org.naho.user.port.out;

import org.naho.user.model.Role;
import org.naho.user.type.RoleName;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(RoleName roleName);
}
