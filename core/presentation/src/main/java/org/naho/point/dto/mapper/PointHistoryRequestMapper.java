package org.naho.point.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.dto.request.PointHistoryQueryRequest;
import org.naho.point.dto.request.PointHistoryRequest;

@Mapper(componentModel = "spring")
public interface PointHistoryRequestMapper {
    PointHistoryCommand requestToCommand(PointHistoryRequest request);

    PointHistoryQueryCommand requestToCommand(PointHistoryQueryRequest request);
}
