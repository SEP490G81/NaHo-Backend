package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record UpdatePersonaRequest(
        @NotBlank String name,
        @NotBlank String prompt,
        Long avatarFileId,
        MarugotoLevel defaultMarugotoLevel,
        FormalityLevel defaultFormalityLevel,
        PersonaStatus status,
        String voiceName,
        Gender gender
) {
}
