package org.naho.persona.port.in;

import org.naho.persona.model.Persona;

public interface UpdatePersonaInputPort {
    Persona updatePersona(Long id, String name, String prompt, Long avatarFileId, Long suggestedConversationStyleId);
}
