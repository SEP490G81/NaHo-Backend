package org.naho.chest.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.chest.command.OpenChestCommand;
import org.naho.chest.dto.request.OpenChestRequest;

@Mapper(componentModel = "spring")
public interface ChestRequestMapper {
    OpenChestCommand requestToCommand(OpenChestRequest request);
}
