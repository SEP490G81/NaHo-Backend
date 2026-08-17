package org.naho.speech.azure.mapper;

import org.naho.speech.azure.model.WordAssessment;
import org.naho.speech.azure.result.WordAssessmentResult;

public class WordAssessmentMapper {
    public WordAssessmentResult domainToResult(WordAssessment domain) {
        return new WordAssessmentResult(
                domain.getId(),
                domain.getWord(),
                domain.getAccuracyScore(),
                domain.getErrorType()
        );
    }
}
