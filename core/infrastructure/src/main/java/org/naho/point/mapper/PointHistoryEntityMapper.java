package org.naho.point.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.model.PointHistory;

@Mapper(componentModel = "spring")
public interface PointHistoryEntityMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "learningPathNode", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    PointHistoryEntity domainToEntity(PointHistory domain);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "learningPathNodeId", source = "learningPathNode.id")
    PointHistory entityToDomain(PointHistoryEntity entity);
}
