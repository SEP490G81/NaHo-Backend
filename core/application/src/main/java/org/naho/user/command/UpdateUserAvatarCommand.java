package org.naho.user.command;

import org.naho.file.result.StoredFile;

public record UpdateUserAvatarCommand(
        Long userId,
        StoredFile storedFile
) {
}
