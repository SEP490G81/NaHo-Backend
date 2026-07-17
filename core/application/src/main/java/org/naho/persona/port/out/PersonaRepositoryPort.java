package org.naho.persona.port.out;

import org.naho.persona.model.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositoryPort {
    List<Persona> findAll();

    Optional<Persona> findById(Long id);

    Persona save(Persona persona);
}
