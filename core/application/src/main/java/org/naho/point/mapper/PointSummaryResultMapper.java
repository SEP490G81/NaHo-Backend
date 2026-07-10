package org.naho.point.mapper;

import org.naho.point.model.PointSummary;
import org.naho.point.result.PointSummaryResult;

public class PointSummaryResultMapper {
    public PointSummaryResult domainToResult(PointSummary domain) {
        if (domain == null) {
            return null;
        }
        return new PointSummaryResult(
                domain.getId(),
                domain.getUserId(),
                domain.getTotalPoint()
        );
    }
}
