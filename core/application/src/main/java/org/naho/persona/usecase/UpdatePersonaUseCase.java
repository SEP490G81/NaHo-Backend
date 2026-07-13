package org.naho.persona.usecase;

import org.naho.persona.model.Persona;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

public class UpdatePersonaUseCase implements UpdatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;

    public UpdatePersonaUseCase(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public Persona updatePersona(Long id, String name, String prompt, Long avatarFileId, Long suggestedConversationStyleId) {
        Persona existing = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona with ID " + id + " not found"));

        Persona updated = Persona.builder()
                .id(existing.getId())
                .name(name)
                .prompt(prompt)
                .avatarFileId(avatarFileId)
                .suggestedConversationStyleId(suggestedConversationStyleId)
                .build();
        return personaRepositoryPort.save(updated);
    }
}
