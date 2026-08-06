package org.naho.file.port.in;

import org.naho.file.result.DownloadedFile;

public interface DownloadFileInputPort {
    DownloadedFile downloadFileFromCloud(String objectKey);
}
