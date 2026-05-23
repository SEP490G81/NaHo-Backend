package org.naho.speech.dto;

public record SpeechAssessmentRequest(
        byte[] audioBytes,
        String referenceText
) {}