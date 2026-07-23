package org.naho.chest.dto.mapper;


import org.mapstruct.Mapper;
import org.naho.chest.dto.response.ChestResponse;
import org.naho.chest.result.ChestResult;

@Mapper(componentModel = "spring")
public interface ChestResponseMapper {
    ChestResponse resultToResponse(ChestResult result);
}
