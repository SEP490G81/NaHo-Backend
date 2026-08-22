package org.naho.persona.usecase;

import org.naho.i18n.message.persona.ConversationStyleDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.ConversationStyleMapper;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.out.ConversationStyleRepositoryPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.ConversationStyleResult;
import org.naho.persona.result.PersonaResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.CommonErrorCode;

import java.util.List;

public class GetPersonaUseCase implements GetPersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ConversationStyleRepositoryPort conversationStyleRepositoryPort;
    private final PersonaResultMapper personaResultMapper;
    private final ConversationStyleMapper conversationStyleMapper;

    public GetPersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            ConversationStyleRepositoryPort conversationStyleRepositoryPort,
            PersonaResultMapper personaResultMapper,
            ConversationStyleMapper conversationStyleMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.conversationStyleRepositoryPort = conversationStyleRepositoryPort;
        this.personaResultMapper = personaResultMapper;
        this.conversationStyleMapper = conversationStyleMapper;
    }

    @Override
    public List<PersonaResult> getAllPersonas() {
        List<Persona> personas = personaRepositoryPort.findAll();
        return personas.stream()
                .map(personaResultMapper::domainToResult)
                .toList();
    }

    @Override
    public PersonaResult getPersonaById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    PersonaDetailMessageKey.PERSONA_ID_NULL
            );
        }

        Persona persona = personaRepositoryPort
                .findById(id)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        id
                ));

        return personaResultMapper.domainToResult(persona);
    }

    @Override
    public ConversationStyleResult getConversationStyleByPersonaId(Long personaId) {
        if (personaId == null) {
            throw new ApplicationException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    PersonaDetailMessageKey.PERSONA_ID_NULL
            );
        }

        PersonaResult persona = getPersonaById(personaId);

        if (persona.suggestedConversationStyle() == null || persona.suggestedConversationStyle().id() == null) {
            throw new ApplicationException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    ConversationStyleDetailMessageKey.CONVERSATION_STYLE_ID_NULL
            );
        }

        Long styleId = persona.suggestedConversationStyle().id();

        ConversationStyle conversationStyle = conversationStyleRepositoryPort
                .findById(styleId)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        ConversationStyleDetailMessageKey.CONVERSATION_STYLE_NOT_FOUND,
                        styleId
                ));

        return conversationStyleMapper.domainToResult(conversationStyle);
    }
}


