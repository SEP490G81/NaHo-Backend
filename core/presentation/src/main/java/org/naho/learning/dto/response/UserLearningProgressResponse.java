package org.naho.learning.dto.response;

import org.naho.user.dto.response.LeaderboardUserResponse;

import java.time.Instant;

public record UserLearningProgressResponse(
        Long id,
        Long farthestAvailableNodeId,
        Long lastLearningNodeId,
        Instant lastLearningAt,
        Integer currentStreak,
        Integer longestStreak,
        Double totalPoint,
        LeaderboardUserResponse leaderboardUser
) {
}
