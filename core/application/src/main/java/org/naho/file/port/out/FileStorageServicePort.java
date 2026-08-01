package org.naho.file.port.out;

import org.naho.file.command.UploadFileCommand;
import org.naho.file.model.StoredFile;

public interface FileStorageServicePort {
    void uploadFileToCloud(UploadFileCommand command);

    void deleteFileInCloud(String objectKey);

    StoredFile saveFileToLocal(Object file);
}
