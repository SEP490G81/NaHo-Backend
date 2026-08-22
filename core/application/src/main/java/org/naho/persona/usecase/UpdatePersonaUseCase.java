package org.naho.persona.usecase;

import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.exception.ApplicationException;

public class UpdatePersonaUseCase implements UpdatePersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public UpdatePersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
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

        Persona updated = Persona.builder()
                .id(existing.getId())
                .name(command.name() != null ? command.name() : existing.getName())
                .prompt(command.prompt() != null ? command.prompt() : existing.getPrompt())
                .avatarFileId(command.avatarFileId() != null ? command.avatarFileId() : existing.getAvatarFileId())
                .defaultMarugotoLevel(command.defaultMarugotoLevel() != null ? command.defaultMarugotoLevel() : existing.getDefaultMarugotoLevel())
                .defaultFormalityLevel(command.defaultFormalityLevel() != null ? command.defaultFormalityLevel() : existing.getDefaultFormalityLevel())
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
