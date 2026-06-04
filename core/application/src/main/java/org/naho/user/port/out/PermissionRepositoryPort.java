package org.naho.user.port.out;

import java.util.List;

public interface PermissionRepositoryPort {
    List<String> findAllPermissionCodeByUserId(Long userId);
}
