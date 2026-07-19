package org.naho.point.dto.response;

import org.naho.point.type.PointTransactionType;

import java.time.Instant;

public record PointHistoryResponse(
        Long id,
        Long userId,
        Long learningPathNodeId,
        Double point,
        PointTransactionType transactionType,
        Instant transactionTime
) {
}
