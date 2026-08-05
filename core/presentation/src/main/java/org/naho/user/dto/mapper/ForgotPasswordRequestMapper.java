package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.ForgotPasswordCommand;
import org.naho.user.dto.request.ForgotPasswordRequest;

@Mapper(componentModel = "spring")
public interface ForgotPasswordRequestMapper {
    ForgotPasswordCommand requestToCommand(ForgotPasswordRequest request);
}