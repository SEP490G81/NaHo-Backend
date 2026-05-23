package org.naho.speech.dto;

import org.naho.speech.model.PronunciationAssessmentResult;
import org.naho.speech.model.WordAssessment;

import java.util.List;

public record SpeechAssessmentResponse(
        String transcript,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore,
        List<WordAssessmentResponse> words
) {
    public static SpeechAssessmentResponse fromDomain(PronunciationAssessmentResult domain) {
        List<WordAssessmentResponse> wordResponses = domain.getWords() == null ? List.of() :
                domain.getWords().stream()
                        .map(w -> new WordAssessmentResponse(w.getWord(), w.getAccuracyScore(), w.getErrorType()))
                        .toList();

        return new SpeechAssessmentResponse(
                domain.getTranscript(),
                domain.getAccuracyScore(),
                domain.getFluencyScore(),
                domain.getCompletenessScore(),
                domain.getPronunciationScore(),
                wordResponses
        );
    }

    public record WordAssessmentResponse(
            String word,
            Double accuracyScore,
            String errorType
    ) {}
}