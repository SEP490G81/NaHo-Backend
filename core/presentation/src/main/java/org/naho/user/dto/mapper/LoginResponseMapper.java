package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.LoginResponse;
import org.naho.user.result.LoginResult;

@Mapper(componentModel = "spring")
public interface LoginResponseMapper {
    LoginResponse resultToResponse(LoginResult result);
}
