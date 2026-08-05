package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.ResetPasswordCommand;
import org.naho.user.dto.request.ResetPasswordRequest;

@Mapper(componentModel = "spring")
public interface ResetPasswordRequestMapper {
    ResetPasswordCommand toCommand(ResetPasswordRequest request);
}
