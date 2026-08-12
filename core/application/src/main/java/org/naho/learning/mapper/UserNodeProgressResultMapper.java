package org.naho.learning.mapper;

import org.naho.learning.model.UserNodeProgress;
import org.naho.learning.result.UserNodeProgressResult;

public class UserNodeProgressResultMapper {
    public UserNodeProgressResult domainToResult(UserNodeProgress domain) {
        return UserNodeProgressResult.builder()
                .id(domain.getId())
                .learningPathNodeId(domain.getLearningPathNodeId())
                .userId(domain.getUserId())
                .bestScore(domain.getBestScore())
                .currentScore(domain.getCurrentScore())
                .attemptCount(domain.getAttemptCount())
                .lastCompletedAt(domain.getLastCompletedAt())
                .status(domain.getStatus())
                .build();
    }
}
