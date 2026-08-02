package org.naho.file.port.out;

import org.naho.file.model.StoredFile;

public interface FileStorageServicePort {
    void uploadFileToCloud(StoredFile file);

    StoredFile saveFileToLocal(Object file);

    void deleteFileInLocal(String localStoragePath);
}
