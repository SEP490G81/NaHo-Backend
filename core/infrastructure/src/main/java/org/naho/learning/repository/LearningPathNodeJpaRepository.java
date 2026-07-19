package org.naho.learning.repository;

import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathNodeJpaRepository extends BaseJpaRepository<LearningPathNodeEntity> {
    List<LearningPathNodeEntity> findAllByObjectiveId(Long objectiveId);

    Optional<LearningPathNodeEntity> findFirstByOrderByGlobalOrderIndexAsc();

    Optional<LearningPathNodeEntity> findByChestId(Long chestId);

    Optional<LearningPathNodeEntity> findBySpeakingQuestion_Id(Long speakingQuestionId);
}

