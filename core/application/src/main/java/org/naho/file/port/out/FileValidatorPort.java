package org.naho.file.port.out;

import java.io.InputStream;

public interface FileValidatorPort {
    String validateImageFile(InputStream inputStream);

    void validateWavFileAndDuration(InputStream inputStream, Double maxDuration);
}
