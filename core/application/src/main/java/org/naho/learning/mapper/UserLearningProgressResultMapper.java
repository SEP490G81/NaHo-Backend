package org.naho.learning.mapper;

import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.result.UserLearningProgressResult;

public class UserLearningProgressResultMapper {
    public UserLearningProgressResult domainToResult(UserLearningProgress domain) {
        return UserLearningProgressResult.builder()
                .id(domain.getId())
                .farthestAvailableNodeId(domain.getFarthestAvailableNodeId())
                .lastLearningNodeId(domain.getLastLearningNodeId())
                .lastLearningAt(domain.getLastLearningAt())
                .currentStreak(domain.getCurrentStreak())
                .longestStreak(domain.getLongestStreak())
                .build();
    }
}
