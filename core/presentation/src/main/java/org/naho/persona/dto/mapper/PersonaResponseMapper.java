package org.naho.persona.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.result.PersonaResult;

@Mapper(componentModel = "spring")
public interface PersonaResponseMapper {
    PersonaResponse resultToResponse(PersonaResult result);
}


