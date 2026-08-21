package org.naho.persona.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.persona.dto.response.ConversationStyleResponse;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.result.PersonaResult;

@Mapper(componentModel = "spring")
public interface PersonaResponseMapper {
    PersonaResponse toResponse(Persona domain);

    PersonaResponse resultToResponse(PersonaResult result);

    ConversationStyleResponse toConversationStyleResponse(ConversationStyle conversationStyle);
}
