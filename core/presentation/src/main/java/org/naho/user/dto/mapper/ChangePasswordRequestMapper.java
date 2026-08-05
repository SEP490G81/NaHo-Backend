package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.command.ChangePasswordCommand;
import org.naho.user.dto.request.ChangePasswordRequest;

@Mapper(componentModel = "spring")
public interface ChangePasswordRequestMapper {
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "oldPassword", source = "request.oldPassword")
    @Mapping(target = "newPassword", source = "request.newPassword")
    @Mapping(target = "confirmPassword", source = "request.confirmPassword")
    ChangePasswordCommand toCommand(Long userId, ChangePasswordRequest request);
}
