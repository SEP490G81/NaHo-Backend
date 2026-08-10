package org.naho.user.port.out;

import org.naho.user.model.Role;
import org.naho.user.type.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    List<Role> findAll();

    Optional<Role> findByName(RoleName roleName);

    Optional<Role> findById(Long id);

    Optional<Role> findByUserId(Long userId);

    List<Role> findAllByUserId(Long userId);

    List<String> findRoleNamesByUserId(Long userId);
}
