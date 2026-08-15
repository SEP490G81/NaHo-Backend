package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.LessonEntity;
import org.naho.book.model.Lesson;

@Mapper(componentModel = "spring")
public interface LessonEntityMapper {

    @Mapping(target = "topicId", source = "topic.id")
    Lesson entityToDomain(LessonEntity entity);

    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "objectives", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    LessonEntity domainToEntity(Lesson domain);

    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "objectives", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    void updateEntityFromDomain(Lesson domain, @org.mapstruct.MappingTarget LessonEntity entity);
}
