package org.naho.file.command;

import java.io.InputStream;

public record SaveFileToLocalCommand(
        String localStoragePath,
        InputStream inputStream,
        String contentType,
        Long size
) {
}
