package org.naho.point.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.point.dto.response.PointSummaryResponse;
import org.naho.point.result.PointSummaryResult;

@Mapper(componentModel = "spring")
public interface PointSummaryResponseMapper {
    PointSummaryResponse resultToResponse(PointSummaryResult result);
}
