package org.naho.persona.port.in;

import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.PersonaStatus;

public interface UpdatePersonaInputPort {
    PersonaResult updatePersona(UpdatePersonaCommand command);

    PersonaStatus updateStatus(Long id);
}


