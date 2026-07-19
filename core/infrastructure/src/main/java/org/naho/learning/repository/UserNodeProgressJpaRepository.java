package org.naho.learning.repository;

import org.naho.learning.entity.UserNodeProgressEntity;
import org.naho.shared.persistence.BaseJpaRepository;

public interface UserNodeProgressJpaRepository extends BaseJpaRepository<UserNodeProgressEntity> {
    boolean existsByLearningPathNode_Id(Long learningPathNodeId);
}
