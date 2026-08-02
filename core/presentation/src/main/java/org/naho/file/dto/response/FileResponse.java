package org.naho.file.dto.response;

public record FileResponse(
        Long id,
        String localStoragePath,
        String objectKey,
        String originalFileName,
        String contentType,
        Long size
) {
}
