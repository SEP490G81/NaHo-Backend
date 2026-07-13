package org.naho.persona.usecase;

import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;

import java.util.List;
import java.util.Optional;

public class GetPersonaUseCase implements GetPersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;

    public GetPersonaUseCase(PersonaRepositoryPort personaRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
    }

    @Override
    public List<Persona> getAllPersonas() {
        return personaRepositoryPort.findAll();
    }

    @Override
    public Optional<Persona> getPersonaById(Long id) {
        return personaRepositoryPort.findById(id);
    }
}
