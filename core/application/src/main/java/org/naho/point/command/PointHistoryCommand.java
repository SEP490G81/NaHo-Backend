package org.naho.point.command;

import org.naho.point.type.PointTransactionType;

public record PointHistoryCommand(
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
