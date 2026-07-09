package org.naho.point.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.point.dto.response.PointHistoryResponse;
import org.naho.point.result.PointHistoryResult;

@Mapper(componentModel = "spring")
public interface PointHistoryResponseMapper {
    PointHistoryResponse resultToResponse(PointHistoryResult result);
}
