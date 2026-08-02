package org.naho.file.port.in;

import org.naho.file.model.StoredFile;

public interface AsyncUploadFileInputPort {
    void uploadFileToCloud(StoredFile file);
}
