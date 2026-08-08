package org.naho.user.mapper;

import org.naho.user.model.AuthProvider;
import org.naho.user.result.AuthProviderResult;

public class AuthProviderResultMapper {
    public AuthProviderResult domainToResult(AuthProvider domain) {
        return AuthProviderResult.builder()
                .id(domain.getId())
                .providerName(domain.getProviderName())
                .avatarUrl(domain.getAvatarUrl())
                .build();
    }
}
