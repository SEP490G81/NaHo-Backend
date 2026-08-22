package org.naho.persona.port.in;

import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.result.PersonaResult;

public interface CreatePersonaInputPort {
    PersonaResult createPersona(CreatePersonaCommand command);
}


