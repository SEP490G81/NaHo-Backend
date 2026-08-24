package org.naho.persona.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.command.CreatePersonaCommand;
import org.naho.persona.command.UpdatePersonaCommand;
import org.naho.persona.dto.mapper.PersonaResponseMapper;
import org.naho.persona.dto.request.CreatePersonaRequest;
import org.naho.persona.dto.request.UpdatePersonaRequest;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.persona.result.PersonaResult;
import org.naho.persona.type.PersonaStatus;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // ROLE: CONTENT_MANAGER, LEARNER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'LEARNER')")
    @GetMapping
    @ApiResponseMessage(message = "Get all personas successfully!")
    public ResponseEntity<List<PersonaResponse>> getAllPersonas() {
        List<PersonaResult> results = getPersonaInputPort.getAllPersonas();

        List<PersonaResponse> responses = results.stream()
                .map(personaResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: CONTENT_MANAGER, LEARNER
    @PreAuthorize("hasAnyRole('CONTENT_MANAGER', 'LEARNER')")
    @GetMapping("/{personaId}")
    @ApiResponseMessage(message = "Get persona successfully!")
    public ResponseEntity<PersonaResponse> findById(
            @PathVariable Long personaId
    ) {
        PersonaResult result = getPersonaInputPort.findById(personaId);
        return ResponseEntity.ok(personaResponseMapper.resultToResponse(result));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PostMapping
    @ApiResponseMessage(message = "Create persona successfully!")
    public ResponseEntity<PersonaResponse> createPersona(
            @Valid @RequestBody CreatePersonaRequest request
    ) {
        CreatePersonaCommand command = new CreatePersonaCommand(
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.defaultMarugotoLevel(),
                request.defaultFormalityLevel(),
                request.status(),
                request.voiceName(),
                request.gender()
        );
        PersonaResult persona = createPersonaInputPort.createPersona(command);
        return ResponseEntity.ok(personaResponseMapper.resultToResponse(persona));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PutMapping("/{personaId}")
    @ApiResponseMessage(message = "Update persona successfully!")
    public ResponseEntity<PersonaResponse> updatePersona(
            @PathVariable Long personaId,
            @Valid @RequestBody UpdatePersonaRequest request
    ) {
        UpdatePersonaCommand command = new UpdatePersonaCommand(
                personaId,
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.defaultMarugotoLevel(),
                request.defaultFormalityLevel(),
                request.status(),
                request.voiceName(),
                request.gender()
        );
        PersonaResult persona = updatePersonaInputPort.updatePersona(command);
        return ResponseEntity.ok(personaResponseMapper.resultToResponse(persona));
    }

    // ROLE: CONTENT_MANAGER
    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PatchMapping("/{personaId}/status")
    @ApiResponseMessage(message = PersonaDetailMessageKey.PERSONA_UPDATE_STATUS_SUCCESS)
    public ResponseEntity<PersonaStatus> updatePersonaStatus(@PathVariable Long personaId) {
        PersonaStatus status = updatePersonaInputPort.updateStatus(personaId);
        return ResponseEntity.ok(status);
    }
}
