package org.naho.speech.azure.result;

public record AudioSpeechResult(
        byte[] audioData,
        String contentType
) {
}
