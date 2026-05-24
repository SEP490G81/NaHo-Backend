package org.naho.speech.azure.mapper;

import org.naho.speech.azure.result.WordAssessmentResult;
import org.naho.speech.model.WordAssessment;

public class WordAssessmentMapper {
    public WordAssessmentResult domainToResult(WordAssessment domain) {
        return new WordAssessmentResult(
                domain.getWord(),
                domain.getAccuracyScore(),
                domain.getErrorType()
        );
    }
}
