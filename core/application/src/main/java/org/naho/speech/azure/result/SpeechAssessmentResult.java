package org.naho.speech.azure.result;

import java.util.List;

public record SpeechAssessmentResult(
        Long id,
        String transcriptText,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        Double averageScore,
        List<WordAssessmentResult> words
) {
}