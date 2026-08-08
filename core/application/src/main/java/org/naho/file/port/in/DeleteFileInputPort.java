package org.naho.file.port.in;

import org.naho.file.model.File;

public interface DeleteFileInputPort {
    void deleteFileInCloud(String objectKey);

    void retryDeleteFileInCloud(File file);
}

