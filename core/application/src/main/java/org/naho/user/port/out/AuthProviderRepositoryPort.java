package org.naho.user.port.out;

import org.naho.user.model.AuthProvider;

import java.util.List;

public interface AuthProviderRepositoryPort {
    List<AuthProvider> findAllByUser_Id(Long userId);
}
