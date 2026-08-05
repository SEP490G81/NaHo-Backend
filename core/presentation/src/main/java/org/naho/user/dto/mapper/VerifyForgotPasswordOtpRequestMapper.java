package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.user.command.VerifyForgotPasswordOtpCommand;
import org.naho.user.dto.request.VerifyForgotPasswordOtpRequest;

@Mapper(componentModel = "spring")
public interface VerifyForgotPasswordOtpRequestMapper {
    VerifyForgotPasswordOtpCommand toCommand(VerifyForgotPasswordOtpRequest request);
}
