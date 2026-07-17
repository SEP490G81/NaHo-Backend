package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.model.UserLearningProgress;

@Mapper(componentModel = "spring")
public interface UserLearningProgressEntityMapper {

    @Mapping(target = "farthestAvailableNodeId", source = "farthestAvailableNode.id")
    @Mapping(target = "lastLearningNodeId", source = "lastLearningNode.id")
    UserLearningProgress entityToDomain(UserLearningProgressEntity entity);

    @Mapping(target = "farthestAvailableNode", ignore = true)
    @Mapping(target = "lastLearningNode", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UserLearningProgressEntity domainToEntity(UserLearningProgress domain);
}
