package org.naho.point.dto.response;

import org.naho.point.type.PointTransactionType;

import java.time.Instant;

public record PointHistoryResponse(
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
