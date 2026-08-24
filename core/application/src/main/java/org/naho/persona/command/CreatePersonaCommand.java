package org.naho.persona.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record CreatePersonaCommand(
        String name,
        String prompt,
        Long avatarFileId,
        MarugotoLevel defaultMarugotoLevel,
        FormalityLevel defaultFormalityLevel,
        PersonaStatus status,
        String voiceName,
        Gender gender
) {
}
