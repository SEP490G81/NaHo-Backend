package org.naho.persona.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.persona.model.Persona;
import org.naho.persona.result.PersonaResult;

public class PersonaResultMapper {
    private final CrudFileInputPort crudFileInputPort;

    public PersonaResultMapper(
            CrudFileInputPort crudFileInputPort
    ) {
        this.crudFileInputPort = crudFileInputPort;
    }

    public PersonaResult domainToResult(Persona domain) {
        if (domain == null) {
            return null;
        }

        FileResult fileResult = domain.getAvatarFileId() != null ?
                crudFileInputPort.findById(domain.getAvatarFileId()) : null;

        return PersonaResult.builder()
                .id(domain.getId())
                .name(domain.getName())
                .prompt(domain.getPrompt())
                .avatarFile(fileResult)
                .defaultMarugotoLevel(domain.getDefaultMarugotoLevel())
                .defaultFormalityLevel(domain.getDefaultFormalityLevel())
                .status(domain.getStatus())
                .voiceName(domain.getVoiceName())
                .gender(domain.getGender())
                .build();
    }
}
