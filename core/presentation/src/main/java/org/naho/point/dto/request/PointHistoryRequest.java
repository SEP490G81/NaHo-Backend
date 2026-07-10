package org.naho.point.dto.request;

import org.naho.point.type.PointTransactionType;

public record PointHistoryRequest(
        Long id,
        Long userId,
        Long questionId,
        Long objectiveId,
        Long lessonId,
        Long topicId,
        Double point,
        PointTransactionType transactionType
) {
}
