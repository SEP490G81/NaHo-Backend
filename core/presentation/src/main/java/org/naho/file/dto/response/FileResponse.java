package org.naho.file.dto.response;

public record FileResponse(
        Long id,
        String objectKey,
        String previewKey,
        String originalName,
        String contentType,
        Long size
) {
}
