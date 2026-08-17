package org.naho.speech.azure.dto.response;

import java.util.List;

public record SpeechAssessmentResponse(
        Long id,
        String transcriptText,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        Double averageScore,
        List<WordAssessmentResponse> words
) {
}
