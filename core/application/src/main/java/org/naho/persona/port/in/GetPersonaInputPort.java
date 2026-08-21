package org.naho.persona.port.in;

import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.result.PersonaResult;

import java.util.List;
import java.util.Optional;

public interface GetPersonaInputPort {
    List<PersonaResult> getAllPersonas();

    Optional<Persona> getPersonaById(Long id);

    Optional<ConversationStyle> getConversationStyleByPersonaId(Long id);
}
