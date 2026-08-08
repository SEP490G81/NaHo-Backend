package org.naho.config.application;

import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.usecase.CreatePersonaUseCase;
import org.naho.persona.usecase.GetPersonaUseCase;
import org.naho.persona.usecase.UpdatePersonaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonaConfig {

    @Bean
    public GetPersonaInputPort getPersonaInputPort(PersonaRepositoryPort personaRepositoryPort,
                                                   ConversationStyleRepositoryPort conversationStyleRepositoryPort) {
        return new GetPersonaUseCase(personaRepositoryPort, conversationStyleRepositoryPort);
    }

    @Bean
    public CreatePersonaInputPort createPersonaInputPort(PersonaRepositoryPort personaRepositoryPort,
                                                         ConversationStyleRepositoryPort conversationStyleRepositoryPort) {
        return new CreatePersonaUseCase(personaRepositoryPort, conversationStyleRepositoryPort);
    }

    @Bean
    public UpdatePersonaInputPort updatePersonaInputPort(PersonaRepositoryPort personaRepositoryPort,
                                                         ConversationStyleRepositoryPort conversationStyleRepositoryPort) {
        return new UpdatePersonaUseCase(personaRepositoryPort, conversationStyleRepositoryPort);
    }
}
