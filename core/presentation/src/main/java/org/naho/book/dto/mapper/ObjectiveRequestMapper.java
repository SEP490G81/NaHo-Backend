package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.command.UpdateObjectiveCommand;
import org.naho.book.dto.request.UpdateObjectiveRequest;

@Mapper(componentModel = "spring")
public interface ObjectiveRequestMapper {
    @Mapping(target = "objectiveId", source = "objectiveId")
    @Mapping(target = "adminUserId", source = "adminUserId")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    UpdateObjectiveCommand toUpdateCommand(UpdateObjectiveRequest request, Long objectiveId, Long adminUserId, boolean isAdminOrManager);
}
