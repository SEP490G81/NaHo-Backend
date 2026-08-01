package org.naho.file.result;

public record FileResult(
        Long id,
        String objectKey,
        String originalFileName,
        String contentType,
        Long size
) {
}
