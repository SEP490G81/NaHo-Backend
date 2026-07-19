package org.naho.persona.port.in;

import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.model.Persona;

public interface UpdatePersonaInputPort {
    Persona updatePersona(UpdatePersonaCommand command);
}
