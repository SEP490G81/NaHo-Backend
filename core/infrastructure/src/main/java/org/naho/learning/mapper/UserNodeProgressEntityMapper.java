package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.i18n.message.learning.LearningPathNodeDetailMessageKey;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.entity.UserNodeProgressEntity;
import org.naho.learning.exception.LearningPathNodeErrorCode;
import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserNodeProgressEntityMapper {

    @Autowired
    private LearningPathNodeJpaRepository learningPathNodeJpaRepository;

    @Mapping(target = "learningPathNode", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    public abstract UserNodeProgressEntity domainToEntity(UserNodeProgress domain);

    @Mapping(target = "learningPathNodeId", source = "learningPathNode.id")
    @Mapping(target = "userId", source = "user.id")
    public abstract UserNodeProgress entityToDomain(UserNodeProgressEntity entity);

    @Named("getLearningPathNodeEntityById")
    protected LearningPathNodeEntity getLearningPathNodeEntityById(Long learningPathNodeId) {
        return learningPathNodeJpaRepository.findById(learningPathNodeId)
                .orElseThrow(() -> new InfrastructureException(
                        LearningPathNodeErrorCode.LEARNING_PATH_NODE_NOT_FOUND,
                        LearningPathNodeDetailMessageKey.LEARNING_PATH_NODE_ID_NOT_FOUND,
                        learningPathNodeId
                ));
    }
}
