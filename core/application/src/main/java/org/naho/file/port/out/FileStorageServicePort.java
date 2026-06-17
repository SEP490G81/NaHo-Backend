package org.naho.file.port.out;

import java.io.InputStream;

public interface FileStorageServicePort {
    void uploadFile(String objectKey,
                    InputStream inputStream,
                    String contentType,
                    Long size);

    void deleteFileByObjectKey(String objectKey);
}
