package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.persona.mapper.ConversationStyleMapper;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetConversationStyleInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.usecase.CreatePersonaUseCase;
import org.naho.persona.usecase.GetConversationStyleUseCase;
import org.naho.persona.usecase.GetPersonaUseCase;
import org.naho.persona.usecase.UpdatePersonaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonaConfig {

    @Bean
    public ConversationStyleMapper conversationStyleMapper() {
        return new ConversationStyleMapper();
    }

    @Bean
    public GetConversationStyleInputPort getConversationStyleInputPort(
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            ConversationStyleMapper conversationStyleMapper
    ) {
        return new GetConversationStyleUseCase(
                conversationStyleRepositoryPort,
                conversationStyleMapper
        );
    }

    @Bean
    public PersonaResultMapper personaResultMapper(
            GetConversationStyleInputPort getConversationStyleInputPort,
            CrudFileInputPort crudFileInputPort
    ) {
        return new PersonaResultMapper(getConversationStyleInputPort, crudFileInputPort);
    }

    @Bean
    public GetPersonaInputPort getPersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper,
            ConversationStyleMapper conversationStyleMapper
    ) {
        return new GetPersonaUseCase(
                personaRepositoryPort,
                conversationStyleRepositoryPort,
                personaResultMapper,
                conversationStyleMapper
        );
    }

    @Bean
    public CreatePersonaInputPort createPersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        return new CreatePersonaUseCase(personaRepositoryPort, conversationStyleRepositoryPort, personaResultMapper);
    }

    @Bean
    public UpdatePersonaInputPort updatePersonaInputPort(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        return new UpdatePersonaUseCase(personaRepositoryPort, conversationStyleRepositoryPort, personaResultMapper);
    }
}


