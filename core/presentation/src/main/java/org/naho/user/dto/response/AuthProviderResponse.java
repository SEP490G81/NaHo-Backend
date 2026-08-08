package org.naho.user.dto.response;

import org.naho.user.type.AuthProviderName;

public record AuthProviderResponse(
        Long id,
        AuthProviderName providerName,
        String avatarUrl
) {
}
