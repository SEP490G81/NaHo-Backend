package org.naho.file.dto.response;

public record FileResponse(
        Long id,
        String objectKey,
        String originalName,
        String contentType,
        Long size
) {
}
