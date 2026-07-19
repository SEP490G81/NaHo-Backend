package org.naho.persona.usecase;

import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

public class CreatePersonaUseCase implements CreatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;

    public CreatePersonaUseCase(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public Persona createPersona(CreatePersonaCommand command) {
        Persona persona = Persona.builder()
                .name(command.name())
                .prompt(command.prompt())
                .avatarFileId(command.avatarFileId())
                .suggestedConversationStyleId(command.suggestedConversationStyleId())
                .build();
        return personaRepositoryPort.save(persona);
    }
}
