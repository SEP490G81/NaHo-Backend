package org.naho.season.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.dto.request.CreateSeasonRequest;

@Mapper(componentModel = "spring")
public interface SeasonRequestMapper {
    CreateSeasonCommand requestToCommand(CreateSeasonRequest request);
}
