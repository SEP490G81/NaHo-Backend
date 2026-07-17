package org.naho.learning.port.out;

import org.naho.learning.model.UserLearningProgress;

import java.util.Optional;

public interface UserLearningProgressRepositoryPort {
    Optional<UserLearningProgress> findUserLearningProgressByUserId(Long userId);

    UserLearningProgress save(UserLearningProgress userLearningProgress, Long userId);
}
