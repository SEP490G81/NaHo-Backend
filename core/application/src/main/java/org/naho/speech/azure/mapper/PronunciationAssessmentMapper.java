package org.naho.speech.azure.mapper;

import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.azure.result.WordAssessmentResult;
import org.naho.speech.model.SpeechAssessment;

import java.util.List;

public class PronunciationAssessmentMapper {

    private final WordAssessmentMapper wordAssessmentMapper;

    public PronunciationAssessmentMapper(WordAssessmentMapper wordAssessmentMapper) {
        this.wordAssessmentMapper = wordAssessmentMapper;
    }

    public SpeechAssessmentResult modelToResult(SpeechAssessment domain) {
        List<WordAssessmentResult> wordAssessmentResults =
                domain.getWords().stream()
                        .map(wordAssessmentMapper::domainToResult)
                        .toList();

        return new SpeechAssessmentResult(
                domain.getId(),
                domain.getTranscriptText(),
                domain.getAccuracyScore(),
                domain.getFluencyScore(),
                domain.getCompletenessScore(),
                domain.getPronunciationScore(),
                wordAssessmentResults
        );
    }
}
