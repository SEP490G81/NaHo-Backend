package org.naho.point.mapper;

import org.naho.point.model.PointHistory;
import org.naho.point.result.PointHistoryResult;

public class PointHistoryResultMapper {
    public PointHistoryResult domainToResult(PointHistory domain) {
        if (domain == null) {
            return null;
        }
        return new PointHistoryResult(
                domain.getId(),
                domain.getUserId(),
                domain.getLearningPathNodeId(),
                domain.getPoint(),
                domain.getTransactionType(),
                domain.getTransactionTime()
        );
    }
}
