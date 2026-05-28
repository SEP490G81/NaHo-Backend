package org.naho.speech.azure.result;

import java.util.List;

public record SpeechAssessmentResult(
        Long id,
//        Long answerHistoryId,
        String transcriptText,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        List<WordAssessmentResult> words
) {
}