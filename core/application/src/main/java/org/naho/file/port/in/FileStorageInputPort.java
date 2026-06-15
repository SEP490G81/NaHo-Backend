package org.naho.file.port.in;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.result.FileResult;

public interface FileStorageInputPort {
    FileResult uploadFile(FileUploadCommand command);

    String deleteById(Long id);
}
