package org.naho.learning.port.out;

import org.naho.learning.model.UserNodeProgress;

public interface UserNodeProgressRepositoryPort {
    boolean existsByLearningPathNodeId(Long learningPathNodeId);

    UserNodeProgress save(UserNodeProgress userNodeProgress);
}
