package org.naho.persona.port.in;

import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.model.Persona;

public interface CreatePersonaInputPort {
    Persona createPersona(CreatePersonaCommand command);
}
