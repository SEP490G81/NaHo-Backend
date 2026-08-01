package org.naho.file.command;

public record SaveFileToDbCommand(
        String folderName,
        String originalFileName,
        String contentType,
        Long size
) {
}
