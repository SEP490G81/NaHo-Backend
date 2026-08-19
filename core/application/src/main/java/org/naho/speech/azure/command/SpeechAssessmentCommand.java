package org.naho.speech.azure.command;

public record SpeechAssessmentCommand(
        byte[] audioBytes,
        double duration,
        String referenceText,
        Long userId
) {
    public SpeechAssessmentCommand(byte[] audioBytes, String referenceText, Long userId) {
        this(audioBytes, 0.0, referenceText, userId);
    }
}

