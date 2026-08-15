package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.command.UpdateLessonCommand;
import org.naho.book.dto.request.UpdateLessonRequest;

@Mapper(componentModel = "spring")
public interface LessonRequestMapper {
    @Mapping(target = "lessonId", source = "lessonId")
    @Mapping(target = "adminUserId", source = "adminUserId")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    UpdateLessonCommand toUpdateCommand(UpdateLessonRequest request, Long lessonId, Long adminUserId, boolean isAdminOrManager);
}
