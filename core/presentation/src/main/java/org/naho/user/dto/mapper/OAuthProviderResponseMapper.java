package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.OAuthProviderResponse;
import org.naho.user.result.OAuthProviderResult;

@Mapper(componentModel = "spring")
public interface OAuthProviderResponseMapper {
    OAuthProviderResponse resultToResponse(OAuthProviderResult result);
}
