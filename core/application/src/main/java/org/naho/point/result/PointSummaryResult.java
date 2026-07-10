package org.naho.point.result;

public record PointSummaryResult(
        Long id,
        Long userId,
        Double totalPoint
) {
}
