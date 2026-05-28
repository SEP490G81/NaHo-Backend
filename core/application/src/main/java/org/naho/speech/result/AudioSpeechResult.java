package org.naho.speech.result;

public record AudioSpeechResult(
        byte[] audioData,
        String contentType
) {}