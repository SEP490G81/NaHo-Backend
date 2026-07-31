package org.naho.file.port.in;

import org.naho.file.command.UploadFileCommand;

public interface FileStorageInputPort {
    void uploadFile(UploadFileCommand command);

    String deleteFileById(Long id);
}
