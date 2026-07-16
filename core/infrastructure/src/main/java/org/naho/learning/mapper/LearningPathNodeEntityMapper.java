package org.naho.learning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.model.LearningPathNode;

@Mapper(componentModel = "spring")
public interface LearningPathNodeEntityMapper {

    @Mapping(target = "objectiveId", source = "objective.id")
    @Mapping(target = "speakingQuestionId", source = "speakingQuestion.id")
    @Mapping(target = "vocabularyQuestionId", source = "vocabularyQuestion.id")
    @Mapping(target = "chestId", source = "chest.id")
    LearningPathNode entityToDomain(LearningPathNodeEntity entity);
}
