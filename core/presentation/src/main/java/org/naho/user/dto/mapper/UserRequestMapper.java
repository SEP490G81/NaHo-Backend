package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.UserQueryCommand;
import org.naho.user.dto.request.UserQueryRequest;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    UserQueryCommand requestToCommand(UserQueryRequest request);
}
