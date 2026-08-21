package org.naho.persona.usecase;

import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;

import java.util.List;
import java.util.Optional;

public class GetPersonaUseCase implements GetPersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public GetPersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
        this.personaResultMapper = personaResultMapper;
    }

    @Override
    public List<PersonaResult> getAllPersonas() {
        List<Persona> personas = personaRepositoryPort.findAll();
        return personas.stream()
                .map(personaResultMapper::domainToResult)
                .toList();
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
//        if (persona.getConversationStyle() != null) {
//            return Optional.of(persona.getConversationStyle());
//        }
        if (persona.getSuggestedConversationStyleId() != null && conversationStyleRepositoryPort != null) {
            return conversationStyleRepositoryPort.findById(persona.getSuggestedConversationStyleId());
        }
        return Optional.empty();
    }
}
