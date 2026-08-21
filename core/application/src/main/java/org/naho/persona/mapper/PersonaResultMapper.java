package org.naho.persona.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetConversationStyleInputPort;
import org.naho.persona.result.ConversationStyleResult;
import org.naho.persona.result.PersonaResult;

public class PersonaResultMapper {
    private final GetConversationStyleInputPort getConversationStyleInputPort;
    private final CrudFileInputPort crudFileInputPort;

    public PersonaResultMapper(
            GetConversationStyleInputPort getConversationStyleInputPort,
            CrudFileInputPort crudFileInputPort
    ) {
        this.getConversationStyleInputPort = getConversationStyleInputPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    public PersonaResult domainToResult(Persona domain) {
        if (domain == null) {
            return null;
        }

        FileResult fileResult = domain.getAvatarFileId() != null ?
                crudFileInputPort.findById(domain.getAvatarFileId()) : null;

        ConversationStyleResult conversationStyleResult = getConversationStyleInputPort
                .findById(domain.getSuggestedConversationStyleId());

        return PersonaResult.builder()
                .id(domain.getId())
                .name(domain.getName())
                .prompt(domain.getPrompt())
                .avatarFile(fileResult)
                .suggestedConversationStyle(conversationStyleResult)
                .status(domain.getStatus())
                .voiceName(domain.getVoiceName())
                .gender(domain.getGender())
                .build();
    }
}
