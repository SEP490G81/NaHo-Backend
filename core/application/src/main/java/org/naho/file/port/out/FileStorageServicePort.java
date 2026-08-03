package org.naho.file.port.out;

import org.naho.file.model.File;
import org.naho.file.model.StoredFile;
import org.naho.file.result.FileResult;

public interface FileStorageServicePort {
    void uploadFileToCloud(StoredFile file, boolean isPublic);

    void deleteFileInCloud(String objectKey, boolean isPublic);

    StoredFile saveFileToLocal(Object file);

    void deleteFileInLocal(String objectKey);

    String generatePresignedUrl(File file);

    FileResult retryUploadFileToCloud(Long id, boolean isPublic);
}
