package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.usecase.CreatePersonaUseCase;
import org.naho.persona.usecase.GetPersonaUseCase;
import org.naho.persona.usecase.UpdatePersonaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonaConfig {

    @Bean
    public PersonaResultMapper personaResultMapper(
            CrudFileInputPort crudFileInputPort
    ) {
        return new PersonaResultMapper(crudFileInputPort);
    }

    @Bean
    public GetPersonaInputPort getPersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        return new GetPersonaUseCase(
                personaRepositoryPort,
                personaResultMapper
        );
    }

    @Bean
    public CreatePersonaInputPort createPersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        return new CreatePersonaUseCase(personaRepositoryPort, personaResultMapper);
    }

    @Bean
    public UpdatePersonaInputPort updatePersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        return new UpdatePersonaUseCase(personaRepositoryPort, personaResultMapper);
    }
}


