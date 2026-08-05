package org.naho.file.port.in;

import org.naho.file.model.File;

public interface AsyncUploadFileInputPort {
    void retryUploadFileToCloudAsync(File file);
}
