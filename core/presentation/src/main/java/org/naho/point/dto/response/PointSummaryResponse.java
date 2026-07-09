package org.naho.point.dto.response;

public record PointSummaryResponse(
        Long id,
        Long userId,
        Double totalPoint
) {
}
