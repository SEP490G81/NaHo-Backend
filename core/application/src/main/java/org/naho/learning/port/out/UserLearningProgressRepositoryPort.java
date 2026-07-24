package org.naho.learning.port.out;

import org.naho.learning.model.UserLearningProgress;

import java.util.Optional;

public interface UserLearningProgressRepositoryPort {
    Optional<UserLearningProgress> findByUserId(Long userId);

    UserLearningProgress save(UserLearningProgress userLearningProgress);

    UserLearningProgress createNew(UserLearningProgress userLearningProgress, Long userId);

    boolean existsByUserId(Long userId);
}
