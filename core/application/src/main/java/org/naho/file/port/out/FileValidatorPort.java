package org.naho.file.port.out;

public interface FileValidatorPort {
    void validateImageFile(byte[] fileBytes);

    double validateWavFileAndDuration(byte[] audioBytes, Double maxDuration);

    double validateAudioFileAndDuration(byte[] audioBytes, Double maxDuration);
}

