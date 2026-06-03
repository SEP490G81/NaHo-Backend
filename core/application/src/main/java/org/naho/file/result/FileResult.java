package org.naho.file.result;

public record FileResult(
        Long id,
        String objectKey,
        String previewKey,
        String originalName,
        String contentType,
        Long size
) {
}
