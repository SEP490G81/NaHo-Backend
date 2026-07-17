package org.naho.learning.dto.response;

import java.time.Instant;

public record UserLearningProgressResponse(
        Long id,
        Long farthestAvailableNodeId,
        Long lastLearningNodeId,
        Instant lastLearningAt,
        Integer currentStreak,
        Integer longestStreak,
        Double totalPoint
) {
}
