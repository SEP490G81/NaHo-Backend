package org.naho.file.result;

public record FileResult(
        Long id,
        String fileUrl,
        String previewUrl,
        String originalName,
        String contentType,
        Long size
) {
}
