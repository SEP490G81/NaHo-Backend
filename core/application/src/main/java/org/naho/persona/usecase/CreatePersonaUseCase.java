package org.naho.persona.usecase;

import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

public class CreatePersonaUseCase implements CreatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;

    public CreatePersonaUseCase(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public Persona createPersona(String name, String prompt, Long avatarFileId, Long suggestedConversationStyleId) {
        Persona persona = Persona.builder()
                .name(name)
                .prompt(prompt)
                .avatarFileId(avatarFileId)
                .suggestedConversationStyleId(suggestedConversationStyleId)
                .build();
        return personaRepositoryPort.save(persona);
    }
}
