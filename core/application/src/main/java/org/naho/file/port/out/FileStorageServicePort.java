package org.naho.file.port.out;

import org.naho.file.model.File;
import org.naho.file.result.DownloadedFile;
import org.naho.file.result.StoredFile;

public interface FileStorageServicePort {
    void uploadFileToCloud(StoredFile file);

    StoredFile saveFileToLocal(Object file, String folder, boolean isPublic);

    void deleteFileInLocal(String objectKey);

    String generatePresignedUrl(File file);

    DownloadedFile downloadFileFromCloud(File file);

    void deleteFileInCloud(File file);

    StoredFile saveReportFileToLocal(Object fileObj);
}
