package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.book.model.Objective;

@Mapper(componentModel = "spring")
public interface ObjectiveEntityMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    Objective entityToDomain(ObjectiveEntity entity);

    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "learningPathNodes", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    ObjectiveEntity domainToEntity(Objective domain);

    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "learningPathNodes", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    void updateEntityFromDomain(Objective domain, @org.mapstruct.MappingTarget ObjectiveEntity entity);
}
