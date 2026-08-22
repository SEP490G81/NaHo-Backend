package org.naho.persona.dto.response;

import org.naho.file.dto.response.FileResponse;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record PersonaResponse(
        Long id,
        String name,
        String prompt,
        FileResponse avatarFile,
        MarugotoLevel defaultMarugotoLevel,
        FormalityLevel defaultFormalityLevel,
        PersonaStatus status,
        String voiceName,
        Gender gender
) {
}
