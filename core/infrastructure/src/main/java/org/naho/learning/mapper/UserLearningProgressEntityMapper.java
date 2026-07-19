package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserLearningProgressEntityMapper {

    @Autowired
    private LearningPathNodeJpaRepository learningPathNodeJpaRepository;

    @Mapping(target = "farthestAvailableNodeGlobalOrderIndex", source = "farthestAvailableNode.globalOrderIndex")
    @Mapping(target = "farthestAvailableNodeId", source = "farthestAvailableNode.id")
    @Mapping(target = "lastLearningNodeId", source = "lastLearningNode.id")
    @Mapping(target = "lastLearningNodeGlobalOrderIndex", source = "lastLearningNode.globalOrderIndex")
    public abstract UserLearningProgress entityToDomain(UserLearningProgressEntity entity);

    @Mapping(
            target = "farthestAvailableNode",
            source = "farthestAvailableNodeId",
            qualifiedByName = "getFarthestAvailableNodeById"
    )
    @Mapping(
            target = "lastLearningNode",
            source = "lastLearningNodeId",
            qualifiedByName = "getLastLearningNodeById"
    )
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    public abstract UserLearningProgressEntity domainToEntity(UserLearningProgress domain);

    @Named("getFarthestAvailableNodeById")
    protected LearningPathNodeEntity getFarthestAvailableNodeById(Long farthestAvailableNodeId) {
        if (farthestAvailableNodeId == null) {
            return null;
        }
        return learningPathNodeJpaRepository.getReferenceById(farthestAvailableNodeId);
    }

    @Named("getLastLearningNodeById")
    protected LearningPathNodeEntity getLastLearningNodeById(Long lastLearningNodeId) {
        if (lastLearningNodeId == null) {
            return null;
        }
        return learningPathNodeJpaRepository.getReferenceById(lastLearningNodeId);
    }
}
