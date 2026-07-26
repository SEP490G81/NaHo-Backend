package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.ResendOtpCommand;
import org.naho.user.dto.request.ResendOtpRequest;

@Mapper(componentModel = "spring")
public interface ResendOtpRequestMapper {
    ResendOtpCommand toCommand(ResendOtpRequest request);
}
