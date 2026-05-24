package org.naho.speech.azure.mapper;

import org.naho.speech.azure.result.PronunciationAssessmentResult;
import org.naho.speech.azure.result.WordAssessmentResult;
import org.naho.speech.model.PronunciationAssessment;

import java.util.List;

public class PronunciationAssessmentMapper {

    private final WordAssessmentMapper wordAssessmentMapper;

    public PronunciationAssessmentMapper(WordAssessmentMapper wordAssessmentMapper) {
        this.wordAssessmentMapper = wordAssessmentMapper;
    }

    public PronunciationAssessmentResult modelToResult(PronunciationAssessment domain) {
        List<WordAssessmentResult> wordAssessmentResults =
                domain.getWords().stream()
                        .map(wordAssessmentMapper::domainToResult)
                        .toList();

        return new PronunciationAssessmentResult(
                domain.getTranscript(),
                domain.getAccuracyScore(),
                domain.getFluencyScore(),
                domain.getCompletenessScore(),
                domain.getPronunciationScore(),
                wordAssessmentResults
        );
    }
}
