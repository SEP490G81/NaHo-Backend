package org.naho.file.port.out;

import org.naho.file.command.FileUploadCommand;

public interface FileStorageServicePort {
    String upload(FileUploadCommand command);
}
