package org.naho.learning.mapper;

import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.result.UserLearningProgressResult;
import org.naho.user.result.LeaderboardUserResult;

public class UserLearningProgressResultMapper {
    public UserLearningProgressResult domainToResult(UserLearningProgress domain) {
        return domainToResult(domain, null);
    }

    public UserLearningProgressResult domainToResult(UserLearningProgress domain, LeaderboardUserResult leaderboardUser) {
        return UserLearningProgressResult.builder()
                .id(domain.getId())
                .farthestAvailableNodeId(domain.getFarthestAvailableNodeId())
                .farthestAvailableNodeGlobalOrderIndex(domain.getFarthestAvailableNodeGlobalOrderIndex())
                .lastLearningNodeId(domain.getLastLearningNodeId())
                .lastLearningNodeGlobalOrderIndex(domain.getLastLearningNodeGlobalOrderIndex())
                .lastLearningAt(domain.getLastLearningAt())
                .currentStreak(domain.getCurrentStreak())
                .longestStreak(domain.getLongestStreak())
                .totalPoint(domain.getTotalPoint())
                .leaderboardUser(leaderboardUser)
                .build();
    }
}
