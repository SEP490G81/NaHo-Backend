package org.naho.learning.dto.response;

import lombok.Builder;
import org.naho.learning.type.NodeStatus;

import java.time.Instant;

@Builder
public record UserNodeProgressResponse(
        Long id,
        Long learningPathNodeId,
        Long userId,
        Double bestScore,
        Double currentScore,
        Integer attemptCount,
        Instant lastCompletedAt,
        NodeStatus status
) {
}
