package org.naho.speech.command;

public record SpeechAssessmentCommand(
        byte[] audioBytes,
        String referenceText
) {}