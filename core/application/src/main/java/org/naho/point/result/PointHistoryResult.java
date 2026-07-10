package org.naho.point.result;

import org.naho.point.type.PointTransactionType;

import java.time.Instant;

public record PointHistoryResult(
        Long id,
        Long userId,
        Long learningPathNodeId,
        Long objectiveId,
        Long lessonId,
        Long topicId,
        Long bookId,
        Double point,
        PointTransactionType transactionType,
        Instant transactionTime
) {
}
