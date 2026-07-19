package org.naho.learning.repository;

import org.naho.learning.entity.UserNodeProgressEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Optional;

public interface UserNodeProgressJpaRepository extends BaseJpaRepository<UserNodeProgressEntity> {
    boolean existsByLearningPathNode_Id(Long learningPathNodeId);

    Optional<UserNodeProgressEntity> findByLearningPathNode_Id(Long learningPathNodeId);

    Optional<UserNodeProgressEntity> findByLearningPathNode_IdAndUser_Id(Long learningPathNodeId, Long userId);
}
