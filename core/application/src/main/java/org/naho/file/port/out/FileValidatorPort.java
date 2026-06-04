package org.naho.file.port.out;

import org.naho.file.command.FileUploadCommand;

public interface FileValidatorPort {
    void validateFileUploadCommand(FileUploadCommand command);
}
