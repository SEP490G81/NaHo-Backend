package org.naho.user.port.out;

import java.util.List;

public interface RoleRepositoryPort {
    List<String> findRoleNamesByUserId(Long userId);
}
