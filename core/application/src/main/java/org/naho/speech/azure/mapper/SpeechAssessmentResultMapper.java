package org.naho.speech.azure.mapper;

import org.naho.speech.azure.model.SpeechAssessment;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.azure.result.WordAssessmentResult;

import java.util.List;

public class SpeechAssessmentResultMapper {
    private final WordAssessmentResultMapper wordAssessmentResultMapper;

    public SpeechAssessmentResultMapper(WordAssessmentResultMapper wordAssessmentResultMapper) {
        this.wordAssessmentResultMapper = wordAssessmentResultMapper;
    }

    public SpeechAssessmentResult modelToResult(SpeechAssessment domain) {
        List<WordAssessmentResult> wordAssessmentResults = domain
                .getWords().stream()
                .map(wordAssessmentResultMapper::domainToResult)
                .toList();

        return new SpeechAssessmentResult(
                domain.getId(),
                domain.getTranscriptText(),
                domain.getAccuracyScore(),
                domain.getFluencyScore(),
                domain.getCompletenessScore(),
                domain.getPronunciationScore(),
                domain.getAverageScore(),
                wordAssessmentResults
        );
    }


}
