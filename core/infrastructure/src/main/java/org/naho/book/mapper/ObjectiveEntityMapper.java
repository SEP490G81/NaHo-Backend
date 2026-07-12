package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.book.model.Objective;

@Mapper(componentModel = "spring")
public interface ObjectiveEntityMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    Objective entityToDomain(ObjectiveEntity entity);
}
