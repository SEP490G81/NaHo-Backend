package org.naho.file.port.in;

import org.naho.file.result.FileResult;

public interface FileOperationRetryInputPort {
    FileResult retryUploadFileToCloud(Long id, boolean isPublic);
}
