package org.naho.persona.usecase;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.exception.ApplicationException;

public class UpdatePersonaUseCase implements UpdatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public UpdatePersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
        this.personaResultMapper = personaResultMapper;
    }

    @Override
    public PersonaResult updatePersona(UpdatePersonaCommand command) {
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
                .status(command.status() != null ? command.status() : existing.getStatus())
                .voiceName(command.voiceName() != null ? command.voiceName() : existing.getVoiceName())
                .gender(command.gender() != null ? command.gender() : existing.getGender())
                .build();

        Persona savedPersona = personaRepositoryPort.save(updated);
        return personaResultMapper.domainToResult(savedPersona);
    }

    @Override
    public PersonaStatus updateStatus(Long id) {
        Persona persona = personaRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        id
                ));

        PersonaStatus newStatus = getNewStatus(persona);
        personaRepositoryPort.updatePersonaStatus(id, newStatus);
        return newStatus;
    }

    /**
     * Lấy trạng thái mới của nhân vật dựa trên trạng thái hiện tại
     * ACTIVE => UNACTIVE
     * UNACTIVE => ACTIVE
     *
     * @param persona nhân vật bị chỉnh sửa trạng thái
     * @return new PersonaStatus
     */
    private PersonaStatus getNewStatus(Persona persona) {
        if (PersonaStatus.ACTIVE.equals(persona.getStatus())) {
            return PersonaStatus.UNACTIVE;
        } else if (PersonaStatus.UNACTIVE.equals(persona.getStatus())) {
            return PersonaStatus.ACTIVE;
        } else {
            throw new ApplicationException(
                    PersonaErrorCode.PERSONA_PERSIST_FAILED,
                    PersonaDetailMessageKey.PERSONA_UPDATE_STATUS_FAILED
            );
        }
    }
}
