package org.naho.season.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.season.dto.response.SeasonResponse;
import org.naho.season.result.SeasonResult;

@Mapper(componentModel = "spring")
public interface SeasonResponseMapper {
    SeasonResponse resultToResponse(SeasonResult result);
}
