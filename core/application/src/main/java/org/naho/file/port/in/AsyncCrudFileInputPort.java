package org.naho.file.port.in;

import org.naho.file.model.File;

public interface AsyncCrudFileInputPort {
    void retryUploadFileToCloudAsync(File file);

    void deleteFileInCloudAsync(String objectKey);

    void retryDeleteFileInCloudAsync(File file);
}
