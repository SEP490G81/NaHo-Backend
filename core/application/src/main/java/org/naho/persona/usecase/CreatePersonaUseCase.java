package org.naho.persona.usecase;

import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;

public class CreatePersonaUseCase implements CreatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public CreatePersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
        this.personaResultMapper = personaResultMapper;
    }

    @Override
    public PersonaResult createPersona(CreatePersonaCommand command) {
        Long styleId = command.suggestedConversationStyleId();

        if (command.conversationStyleCommand() != null && conversationStyleRepositoryPort != null) {
            ConversationStyle newStyle = ConversationStyle.builder()
                    .description(command.conversationStyleCommand().description())
                    .prompt(command.conversationStyleCommand().prompt())
                    .formalityLevel(command.conversationStyleCommand().formalityLevel())
                    .marugotoLevel(command.conversationStyleCommand().marugotoLevel())
                    .build();
            ConversationStyle savedStyle = conversationStyleRepositoryPort.save(newStyle);
            styleId = savedStyle.getId();
        }

        Persona persona = Persona.builder()
                .name(command.name())
                .prompt(command.prompt())
                .avatarFileId(command.avatarFileId())
                .suggestedConversationStyleId(styleId)
                .status(command.status())
                .voiceName(command.voiceName())
                .gender(command.gender())
                .build();

        Persona savedPersona = personaRepositoryPort.save(persona);
        return personaResultMapper.domainToResult(savedPersona);
    }
}
