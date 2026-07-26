package org.naho.persona.usecase;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;

public class UpdatePersonaUseCase implements UpdatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;

    public UpdatePersonaUseCase(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public Persona updatePersona(UpdatePersonaCommand command) {
        Persona existing = personaRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        command.id()
                ));

        Persona updated = Persona.builder()
                .id(existing.getId())
                .name(command.name())
                .prompt(command.prompt())
                .avatarFileId(command.avatarFileId())
                .suggestedConversationStyleId(command.suggestedConversationStyleId())
                .build();
        return personaRepositoryPort.save(updated);
    }
}
