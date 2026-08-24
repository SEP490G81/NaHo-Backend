package org.naho.persona.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.mapper.PersonaResultMapper;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.result.PersonaResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.exception.CommonErrorCode;

import java.util.List;

public class GetPersonaUseCase implements GetPersonaInputPort {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PersonaResultMapper personaResultMapper;

    public GetPersonaUseCase(
            PersonaRepositoryPort personaRepositoryPort,
            PersonaResultMapper personaResultMapper
    ) {
        this.personaRepositoryPort = personaRepositoryPort;
        this.personaResultMapper = personaResultMapper;
    }

    @Override
    public List<PersonaResult> getAllPersonas() {
        List<Persona> personas = personaRepositoryPort.findAll();
        return personas.stream()
                .map(personaResultMapper::domainToResult)
                .toList();
    }

    @Override
    public PersonaResult findById(Long id) {
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
    public PersonaResult findBySessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        return personaRepositoryPort
                .findBySessionCode(sessionCode)
                .map(personaResultMapper::domainToResult)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        sessionCode
                ));
    }
}


