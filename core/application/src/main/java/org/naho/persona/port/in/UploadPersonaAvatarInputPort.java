package org.naho.persona.port.in;

import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;

public interface UploadPersonaAvatarInputPort {
    FileResult uploadAvatar(StoredFile storedFile);
}
