package org.naho.learning.port.in;

import org.naho.learning.result.UserNodeProgressResult;

public interface CrudUserNodeProgressPort {
    UserNodeProgressResult findByLearningPathNodeIdAndUserId(Long learningPathNodeId, Long userId);
}
