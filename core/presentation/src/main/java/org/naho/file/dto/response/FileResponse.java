package org.naho.file.dto.response;

public record FileResponse(
        Long id,
        String accessUrl,
        String originalFileName,
        String contentType,
        Long size
) {
}
