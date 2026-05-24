package org.naho.speech.azure.result;

import java.util.List;

public record PronunciationAssessmentResult(
        String transcript,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        List<WordAssessmentResult> words
) {
}