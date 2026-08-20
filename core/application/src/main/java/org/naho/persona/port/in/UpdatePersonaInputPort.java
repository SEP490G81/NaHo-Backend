package org.naho.persona.port.in;

import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.model.Persona;
import org.naho.persona.type.PersonaStatus;

public interface UpdatePersonaInputPort {
    Persona updatePersona(UpdatePersonaCommand command);

    PersonaStatus updateStatus(Long id);
}


