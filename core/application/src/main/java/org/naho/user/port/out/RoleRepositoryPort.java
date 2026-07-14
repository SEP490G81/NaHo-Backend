package org.naho.user.port.out;

import org.naho.user.model.Role;
import org.naho.user.type.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    List<String> findRoleNamesByUserId(Long userId);

    List<Role> findAll();

    Optional<Role> findByName(RoleName roleName);

    List<Role> findAllByUserId(Long userId);
}
