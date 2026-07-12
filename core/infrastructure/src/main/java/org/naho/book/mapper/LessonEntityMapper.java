package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.LessonEntity;
import org.naho.book.model.Lesson;

@Mapper(componentModel = "spring")
public interface LessonEntityMapper {

    @Mapping(target = "topicId", source = "topic.id")
    Lesson entityToDomain(LessonEntity entity);
}
