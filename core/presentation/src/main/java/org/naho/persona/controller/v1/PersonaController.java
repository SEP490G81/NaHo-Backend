package org.naho.persona.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.CreateConversationStyleCommand;
import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.command.UpdateConversationStyleCommand;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.dto.mapper.PersonaResponseMapper;
import org.naho.persona.dto.request.CreatePersonaRequest;
import org.naho.persona.dto.request.UpdatePersonaRequest;
import org.naho.persona.dto.response.ConversationStyleResponse;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final GetPersonaInputPort getPersonaInputPort;
    private final CreatePersonaInputPort createPersonaInputPort;
    private final UpdatePersonaInputPort updatePersonaInputPort;
    private final PersonaResponseMapper personaResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_GET_ALL_SUCCESS)
    public ResponseEntity<List<PersonaResponse>> getAllPersonas() {
        List<Persona> personas = getPersonaInputPort.getAllPersonas();
        return ResponseEntity.ok(personaResponseMapper.toResponseList(personas));
    }

    @GetMapping("/{personaId}")
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_GET_SUCCESS)
    public ResponseEntity<PersonaResponse> getPersonaById(
            @PathVariable("personaId") Long personaId
    ) {
        Persona persona = getPersonaInputPort.getPersonaById(personaId)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        personaId
                ));
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }

    @GetMapping("/{personaId}/conversation-style")
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_GET_CONVERSATION_STYLE_SUCCESS)
    public ResponseEntity<ConversationStyleResponse> getConversationStyleByPersonaId(
            @PathVariable("personaId") Long personaId
    ) {
        ConversationStyle style = getPersonaInputPort.getConversationStyleByPersonaId(personaId)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        personaId
                ));
        return ResponseEntity.ok(personaResponseMapper.toConversationStyleResponse(style));
    }

    @PostMapping
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_CREATE_SUCCESS)
    public ResponseEntity<PersonaResponse> createPersona(
            @Valid @RequestBody CreatePersonaRequest request
    ) {
        CreateConversationStyleCommand styleCommand = null;
        if (request.conversationStyle() != null) {
            styleCommand = new CreateConversationStyleCommand(
                    request.conversationStyle().description(),
                    request.conversationStyle().prompt(),
                    request.conversationStyle().formalityLevel(),
                    request.conversationStyle().marugotoLevel()
            );
        }

        CreatePersonaCommand command = new CreatePersonaCommand(
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.suggestedConversationStyleId(),
                styleCommand
        );
        Persona persona = createPersonaInputPort.createPersona(command);
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }

    @PutMapping("/{personaId}")
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_UPDATE_SUCCESS)
    public ResponseEntity<PersonaResponse> updatePersona(
            @PathVariable("personaId") Long personaId,
            @Valid @RequestBody UpdatePersonaRequest request
    ) {
        UpdateConversationStyleCommand styleCommand = null;
        if (request.conversationStyle() != null) {
            styleCommand = new UpdateConversationStyleCommand(
                    request.conversationStyle().id(),
                    request.conversationStyle().description(),
                    request.conversationStyle().prompt(),
                    request.conversationStyle().formalityLevel(),
                    request.conversationStyle().marugotoLevel()
            );
        }

        UpdatePersonaCommand command = new UpdatePersonaCommand(
                personaId,
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.suggestedConversationStyleId(),
                styleCommand
        );
        Persona persona = updatePersonaInputPort.updatePersona(command);
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }
}
