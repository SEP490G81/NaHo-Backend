package org.naho.learning.repository;

import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.type.NodeType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathNodeJpaRepository extends BaseJpaRepository<LearningPathNodeEntity> {
    List<LearningPathNodeEntity> findAllByObjectiveId(Long objectiveId);

    Optional<LearningPathNodeEntity> findFirstByOrderByGlobalOrderIndexAsc();

    Optional<LearningPathNodeEntity> findByChestId(Long chestId);

    Optional<LearningPathNodeEntity> findBySpeakingQuestion_Id(Long speakingQuestionId);

    Optional<LearningPathNodeEntity> findByIdAndNodeType(Long id, NodeType nodeType);

    Optional<LearningPathNodeEntity> findTopByGlobalOrderIndexGreaterThanOrderByGlobalOrderIndex(Double globalOrderIndex);

    Optional<LearningPathNodeEntity> findByVocabularyQuestion_Id(Long vocabularyQuestionId);
}

