package org.naho.user.command;

import org.naho.file.command.FileUploadCommand;
import org.naho.user.type.Gender;
import org.naho.user.valueobject.Dob;

public record UpdateUserInfoCommand(
        Long id,
        String username,
        FileUploadCommand avatarFile,
        String fullName,
        Gender gender,
        Dob dob
) {
}
