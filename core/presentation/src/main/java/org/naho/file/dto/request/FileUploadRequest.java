package org.naho.file.dto.request;

import java.io.InputStream;

public record FileUploadRequest(
        String originalName,
        InputStream inputStream,
        String contentType,
        Long size
) {
}
