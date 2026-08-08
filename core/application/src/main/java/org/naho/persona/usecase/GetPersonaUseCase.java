package org.naho.persona.usecase;

import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

import java.util.List;
import java.util.Optional;

public class GetPersonaUseCase implements GetPersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    public GetPersonaUseCase(PersonaRepositoryPort personaRepositoryPort,
                             ConversationStyleRepositoryPort conversationStyleRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
    }

    @Override
    public List<Persona> getAllPersonas() {
        return personaRepositoryPort.findAll();
    }

    @Override
    public Optional<Persona> getPersonaById(Long id) {
        return personaRepositoryPort.findById(id);
    }

    @Override
    public Optional<ConversationStyle> getConversationStyleByPersonaId(Long id) {
        Optional<Persona> personaOpt = personaRepositoryPort.findById(id);
        if (personaOpt.isEmpty()) {
            return Optional.empty();
        }
        Persona persona = personaOpt.get();
        if (persona.getConversationStyle() != null) {
            return Optional.of(persona.getConversationStyle());
        }
        if (persona.getSuggestedConversationStyleId() != null && conversationStyleRepositoryPort != null) {
            return conversationStyleRepositoryPort.findById(persona.getSuggestedConversationStyleId());
        }
        return Optional.empty();
    }
}
