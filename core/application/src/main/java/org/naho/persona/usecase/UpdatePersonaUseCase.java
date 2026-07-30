package org.naho.persona.usecase;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;

public class UpdatePersonaUseCase implements UpdatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;

    public UpdatePersonaUseCase(PersonaRepositoryPort personaRepositoryPort,
                                ConversationStyleRepositoryPort conversationStyleRepositoryPort) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
    }

    @Override
    public Persona updatePersona(UpdatePersonaCommand command) {
        Persona existing = personaRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        command.id()
                ));

        Long styleId = command.suggestedConversationStyleId() != null
                ? command.suggestedConversationStyleId()
                : existing.getSuggestedConversationStyleId();

        if (command.conversationStyleCommand() != null && conversationStyleRepositoryPort != null) {
            Long styleToUpdateId = command.conversationStyleCommand().id() != null
                    ? command.conversationStyleCommand().id()
                    : styleId;

            ConversationStyle styleToSave = ConversationStyle.builder()
                    .id(styleToUpdateId)
                    .description(command.conversationStyleCommand().description())
                    .prompt(command.conversationStyleCommand().prompt())
                    .formalityLevel(command.conversationStyleCommand().formalityLevel())
                    .marugotoLevel(command.conversationStyleCommand().marugotoLevel())
                    .build();

            ConversationStyle savedStyle = conversationStyleRepositoryPort.save(styleToSave);
            styleId = savedStyle.getId();
        }

        Persona updated = Persona.builder()
                .id(existing.getId())
                .name(command.name())
                .prompt(command.prompt())
                .avatarFileId(command.avatarFileId())
                .suggestedConversationStyleId(styleId)
                .build();

        return personaRepositoryPort.save(updated);
    }
}
