package org.naho.file.dto.response;

public record FileResponse(
        Long id,
        String fileUrl,
        String previewUrl,
        String originalName,
        String contentType,
        Long size
) {
}
