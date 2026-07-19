package org.naho.learning.port.out;

import org.naho.learning.model.LearningPathNode;

import java.util.List;
import java.util.Optional;

public interface LearningPathNodeRepositoryPort {
    List<LearningPathNode> findByObjectiveId(Long objectiveId);

    Optional<LearningPathNode> findById(Long id);

    Optional<LearningPathNode> findFirstLearningPathNode();

    Optional<LearningPathNode> findByChestId(Long chestId);
}
