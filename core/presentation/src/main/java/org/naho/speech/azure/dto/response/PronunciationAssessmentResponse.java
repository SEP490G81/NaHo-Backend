package org.naho.speech.azure.dto.response;

import java.util.List;

public record PronunciationAssessmentResponse(
        String transcript,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        List<WordAssessmentResponse> words
) {
}
