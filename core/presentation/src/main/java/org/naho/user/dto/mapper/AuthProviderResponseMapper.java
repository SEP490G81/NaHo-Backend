package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.AuthProviderResponse;
import org.naho.user.result.AuthProviderResult;

@Mapper(componentModel = "spring")
public interface AuthProviderResponseMapper {
    AuthProviderResponse resultToResponse(AuthProviderResult result);
}
