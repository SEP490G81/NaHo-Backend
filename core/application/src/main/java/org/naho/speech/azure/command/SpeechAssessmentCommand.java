package org.naho.speech.azure.command;

public record SpeechAssessmentCommand(
        byte[] audioBytes,
        String referenceText
) {
}