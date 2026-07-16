package org.naho.user.mapper;

import org.naho.user.model.OAuthProvider;
import org.naho.user.result.OAuthProviderResult;

public class OAuthProviderResultMapper {
    public OAuthProviderResult domainToResult(OAuthProvider domain) {
        return OAuthProviderResult.builder()
                .id(domain.getId())
                .providerName(domain.getProviderName())
                .avatarUrl(domain.getAvatarUrl())
                .build();
    }
}
