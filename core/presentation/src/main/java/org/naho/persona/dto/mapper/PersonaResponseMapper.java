package org.naho.persona.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.model.Persona;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonaResponseMapper {
    PersonaResponse toResponse(Persona domain);

    List<PersonaResponse> toResponseList(List<Persona> domains);
}
