package org.naho.speech.azure.result;

public record WordAssessmentResult(
        String word,
        Double accuracyScore,
        String errorType
) {
}
