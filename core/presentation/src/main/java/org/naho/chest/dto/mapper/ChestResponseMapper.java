package org.naho.chest.dto.mapper;


import org.mapstruct.Mapper;
import org.naho.chest.dto.response.ChestResponse;
import org.naho.chest.dto.response.OpenChestResponse;
import org.naho.chest.result.ChestResult;
import org.naho.chest.result.OpenChestResult;

@Mapper(componentModel = "spring")
public interface ChestResponseMapper {
    ChestResponse resultToResponse(ChestResult result);

    OpenChestResponse resultToResponse(OpenChestResult result);
}
