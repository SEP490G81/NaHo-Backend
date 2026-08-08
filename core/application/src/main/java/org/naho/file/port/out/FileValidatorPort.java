package org.naho.file.port.out;

public interface FileValidatorPort {
    void validateImageFile(byte[] fileBytes);

    void validateWavFileAndDuration(byte[] audioBytes, Double maxDuration);

    double calculateWavDurationSeconds(byte[] audioBytes);
}

