package org.naho.point.command;

import org.naho.point.type.PointTransactionType;

public record PointHistoryCommand(
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
