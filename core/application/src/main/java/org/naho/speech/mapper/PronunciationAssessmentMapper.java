package org.naho.speech.mapper;

import org.naho.speech.model.PronunciationAssessment;
import org.naho.speech.result.PronunciationAssessmentResult;

import java.util.List;

public class PronunciationAssessmentMapper {

    public PronunciationAssessmentResult modelToResult(PronunciationAssessment domain) {
        List<PronunciationAssessmentResult.WordAssessmentResponse> wordResponses = domain.getWords() == null ? List.of() :
                domain.getWords().stream()
                        .map(w -> new PronunciationAssessmentResult.WordAssessmentResponse(w.getWord(), w.getAccuracyScore(), w.getErrorType()))
                        .toList();

        return new PronunciationAssessmentResult(
                domain.getTranscript(),
                domain.getAccuracyScore(),
                domain.getFluencyScore(),
                domain.getCompletenessScore(),
                domain.getPronunciationScore(),
                wordResponses
        );
    }
}
