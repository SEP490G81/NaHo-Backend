package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.model.PointHistory;

@Mapper(componentModel = "spring")
public interface PointHistoryEntityMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "objective", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "topic", ignore = true)
    PointHistoryEntity domainToEntity(PointHistory domain);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "objectiveId", source = "objective.id")
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "topicId", source = "topic.id")
    PointHistory entityToDomain(PointHistoryEntity entity);
}
