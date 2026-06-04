package org.naho.file.port.out;

import java.io.InputStream;

public interface FileStorageServicePort {
    void upload(String objectKey,
                InputStream inputStream,
                String contentType,
                Long size);

    void delete(String objectKey);
}
