package org.naho.user.dto.response;

import org.naho.user.type.OAuthProviderName;

public record OAuthProviderResponse(
        Long id,
        OAuthProviderName providerName,
        String avatarUrl
) {
}
