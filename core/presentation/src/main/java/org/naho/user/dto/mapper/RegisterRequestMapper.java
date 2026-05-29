package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.RegisterCommand;
import org.naho.user.dto.request.RegisterRequest;

@Mapper(componentModel = "spring")
public interface RegisterRequestMapper {
    RegisterCommand requestToCommand(RegisterRequest request);
}
