package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.RegisterResponse;
import org.naho.user.result.RegisterResult;

@Mapper(componentModel = "spring")
public interface RegisterResponseMapper {
    RegisterResponse resultToResponse(RegisterResult result);
}
