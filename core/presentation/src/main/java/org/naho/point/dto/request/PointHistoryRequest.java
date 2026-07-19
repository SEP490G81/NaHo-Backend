package org.naho.point.dto.request;

import org.naho.point.type.PointTransactionType;

public record PointHistoryRequest(
        Long id,
        Long userId,
        Long learningPathNodeId,
        Double point,
        PointTransactionType transactionType
) {
}
