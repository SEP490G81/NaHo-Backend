package org.naho.persona.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.persona.dto.mapper.PersonaResponseMapper;
import org.naho.persona.dto.request.CreatePersonaRequest;
import org.naho.persona.dto.request.UpdatePersonaRequest;
import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.model.Persona;
import org.naho.persona.port.in.CreatePersonaInputPort;
import org.naho.persona.port.in.GetPersonaInputPort;
import org.naho.persona.port.in.UpdatePersonaInputPort;
import org.naho.shared.annotation.ApiResponseMessage;
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
    @ApiResponseMessage(message = "Get all personas successfully!")
    public ResponseEntity<List<PersonaResponse>> getAllPersonas() {
        List<Persona> personas = getPersonaInputPort.getAllPersonas();
        return ResponseEntity.ok(personaResponseMapper.toResponseList(personas));
    }

    @GetMapping("/{personaId}")
    @ApiResponseMessage(message = "Get persona successfully!")
    public ResponseEntity<PersonaResponse> getPersonaById(
            @PathVariable("personaId") Long personaId
    ) {
        Persona persona = getPersonaInputPort.getPersonaById(personaId)
                .orElseThrow(() -> new IllegalArgumentException("Persona with ID " + personaId + " not found"));
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }

    @PostMapping
    @ApiResponseMessage(message = "Create persona successfully!")
    public ResponseEntity<PersonaResponse> createPersona(
            @Valid @RequestBody CreatePersonaRequest request
    ) {
        Persona persona = createPersonaInputPort.createPersona(
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.suggestedConversationStyleId()
        );
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }

    @PutMapping("/{personaId}")
    @ApiResponseMessage(message = "Update persona successfully!")
    public ResponseEntity<PersonaResponse> updatePersona(
            @PathVariable("personaId") Long personaId,
            @Valid @RequestBody UpdatePersonaRequest request
    ) {
        Persona persona = updatePersonaInputPort.updatePersona(
                personaId,
                request.name(),
                request.prompt(),
                request.avatarFileId(),
                request.suggestedConversationStyleId()
        );
        return ResponseEntity.ok(personaResponseMapper.toResponse(persona));
    }
}
