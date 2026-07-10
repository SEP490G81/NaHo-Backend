package org.naho.point.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.point.command.PointSummaryCommand;
import org.naho.point.dto.request.PointSummaryRequest;

@Mapper(componentModel = "spring")
public interface PointSummaryRequestMapper {
    PointSummaryCommand requestToCommand(PointSummaryRequest request);
}
