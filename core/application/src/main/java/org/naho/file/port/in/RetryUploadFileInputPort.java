package org.naho.file.port.in;

import org.naho.file.model.File;

public interface RetryUploadFileInputPort {
    void retryUploadFileToCloud(File file);
}
