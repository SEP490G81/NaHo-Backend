package org.naho.file.port.out;

import org.naho.file.model.File;
import org.naho.file.model.StoredFile;

public interface FileStorageServicePort {
    void uploadFileToCloud(StoredFile file, boolean isPublic);

    void deleteFileInCloud(String objectKey, boolean isPublic);

    StoredFile saveFileToLocal(Object file);

    StoredFile saveReportFileToLocal(Object file);

    void deleteFileInLocal(String localStoragePath);

    String generatePresignedUrl(File file);
}
