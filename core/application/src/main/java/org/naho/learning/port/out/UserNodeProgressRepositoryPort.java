package org.naho.learning.port.out;

import org.naho.learning.model.UserNodeProgress;

import java.util.Optional;

public interface UserNodeProgressRepositoryPort {
    boolean existsByLearningPathNodeIdAndUserId(Long learningPathNodeId, Long userId);

    UserNodeProgress save(UserNodeProgress userNodeProgress);

    Optional<UserNodeProgress> findByLearningPathNodeIdAndUserId(Long learningPathNodeId, Long userId);
}
