package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.CredentialsLoginCommand;
import org.naho.user.dto.request.CredentialsLoginRequest;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {
    CredentialsLoginCommand requestToCommand(CredentialsLoginRequest request);
}
