package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.dto.response.RoleResponse;
import org.naho.user.result.RoleResult;

@Mapper(componentModel = "spring")
public interface RoleResponseMapper {
    RoleResponse resultToResponse(RoleResult result);
}
