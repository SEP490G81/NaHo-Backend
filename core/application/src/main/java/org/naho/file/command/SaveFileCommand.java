package org.naho.file.command;

public record SaveFileCommand(
        String folderName,
        String originalName,
        String contentType,
        Long size
) {
}
