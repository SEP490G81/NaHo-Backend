package org.naho.point.result;

import org.naho.point.type.PointTransactionType;

import java.time.Instant;

public record PointHistoryResult(
        Long id,
        Long userId,
        Long questionId,
        Long objectiveId,
        Long lessonId,
        Long topicId,
        Double point,
        PointTransactionType transactionType,
        Instant transactionTime
) {
}
