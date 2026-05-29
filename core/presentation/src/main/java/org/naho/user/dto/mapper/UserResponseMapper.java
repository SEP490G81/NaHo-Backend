package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.result.UserResult;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    UserResponse resultToResponse(UserResult result);

}
