package org.naho.file.port.in;

import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;

public interface UploadFileInputPort {
    FileResult uploadFileToCloud(StoredFile storedFile);
}
