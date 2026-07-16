package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.model.PointHistory;

@Mapper(componentModel = "spring")
public interface PointHistoryEntityMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "learningPathNode", ignore = true)
    @Mapping(target = "objective", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    PointHistoryEntity domainToEntity(PointHistory domain);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "learningPathNodeId", source = "learningPathNode.id")
    @Mapping(target = "objectiveId", source = "objective.id")
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "bookId", source = "book.id")
    PointHistory entityToDomain(PointHistoryEntity entity);
}
