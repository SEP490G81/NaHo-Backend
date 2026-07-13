package org.naho.persona.port.in;

import org.naho.persona.model.Persona;

public interface CreatePersonaInputPort {
    Persona createPersona(String name, String prompt, Long avatarFileId, Long suggestedConversationStyleId);
}
