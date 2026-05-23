package org.naho.speech.result;

import org.naho.speech.model.PronunciationAssessment;

import java.util.List;

public record PronunciationAssessmentResult(
        String transcript,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        List<WordAssessmentResponse> words
) {

    public record WordAssessmentResponse(
            String word,
            Double accuracyScore,
            String errorType
    ) {}
}