package org.naho.user.port.out;

import org.naho.user.model.Role;
import java.util.List;

public interface RoleRepositoryPort {
    List<String> findRoleNamesByUserId(Long userId);

    List<Role> findAll();
}
