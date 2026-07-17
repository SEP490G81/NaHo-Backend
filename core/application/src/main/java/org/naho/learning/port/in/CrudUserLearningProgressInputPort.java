package org.naho.learning.port.in;

import org.naho.learning.result.UserLearningProgressResult;

public interface CrudUserLearningProgressInputPort {
    UserLearningProgressResult findUserLearningProgressByUserId(Long userId);
}
