package org.naho.speech.azure.dto.response;

public record WordAssessmentResponse(
        String word,
        Double accuracyScore,
        String errorType
) {
}
