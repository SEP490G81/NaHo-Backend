package org.naho.file.port.out;

public interface FileAudioConvertPort {
    byte[] convertToWav(byte[] audioBytes);
}
