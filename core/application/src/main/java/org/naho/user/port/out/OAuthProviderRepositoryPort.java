package org.naho.user.port.out;

import org.naho.user.model.OAuthProvider;

import java.util.List;

public interface OAuthProviderRepositoryPort {
    List<OAuthProvider> findAllByUser_Id(Long userId);
}
