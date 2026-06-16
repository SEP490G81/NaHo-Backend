package org.naho.file.result;

public record FileResult(
        Long id,
        String objectKey,
        String originalName,
        String contentType,
        Long size
) {
}
