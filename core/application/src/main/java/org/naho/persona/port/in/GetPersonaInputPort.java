package org.naho.persona.port.in;

import org.naho.persona.model.Persona;

import java.util.List;
import java.util.Optional;

public interface GetPersonaInputPort {
    List<Persona> getAllPersonas();

    Optional<Persona> getPersonaById(Long id);
}
