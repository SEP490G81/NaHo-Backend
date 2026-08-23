package org.naho.persona.port.in;

import org.naho.persona.result.PersonaResult;

import java.util.List;

public interface GetPersonaInputPort {
    List<PersonaResult> getAllPersonas();

    PersonaResult findById(Long id);
}

