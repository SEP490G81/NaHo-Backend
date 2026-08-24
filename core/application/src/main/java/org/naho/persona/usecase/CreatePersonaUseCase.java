package org.naho.persona.usecase;

import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;

public class CreatePersonaUseCase implements CreatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public CreatePersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.personaResultMapper = personaResultMapper;
    }

    @Override
    public PersonaResult createPersona(CreatePersonaCommand command) {
        Persona persona = Persona.builder()
                .name(command.name())
                .prompt(command.prompt())
                .avatarFileId(command.avatarFileId())
                .defaultMarugotoLevel(command.defaultMarugotoLevel())
                .defaultFormalityLevel(command.defaultFormalityLevel())
                .status(command.status())
                .voiceName(command.voiceName())
                .gender(command.gender())
                .build();

        Persona savedPersona = personaRepositoryPort.save(persona);
        return personaResultMapper.domainToResult(savedPersona);
    }
}
