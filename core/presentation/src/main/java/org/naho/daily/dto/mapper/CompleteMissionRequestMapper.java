package org.naho.daily.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.daily.command.CompleteMissionCommand;
import org.naho.daily.dto.request.CompleteMissionRequest;

@Mapper(componentModel = "spring")
public interface CompleteMissionRequestMapper {

    @Mapping(target = "userId", source = "userId")
    CompleteMissionCommand requestToCommand(CompleteMissionRequest request, Long userId);
}
