package org.naho.point.dto.request;

import org.naho.point.type.PointTransactionType;

public record PointHistoryRequest(
        Long id,
        Long userId,
        Long learningPathNodeId,
        Long objectiveId,
        Long lessonId,
        Long topicId,
        Long bookId,
        Double point,
        PointTransactionType transactionType
) {
}
