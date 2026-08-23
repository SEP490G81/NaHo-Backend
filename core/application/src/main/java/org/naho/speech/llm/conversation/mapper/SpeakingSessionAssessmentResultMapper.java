package org.naho.speech.llm.conversation.mapper;

import org.naho.speech.llm.conversation.result.SpeakingImprovedExpressionResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;

import java.util.List;

public class SpeakingSessionAssessmentResultMapper {

    private final SpeakingImprovedExpressionResultMapper speakingImprovedExpressionResultMapper;

    public SpeakingSessionAssessmentResultMapper(
            SpeakingImprovedExpressionResultMapper speakingImprovedExpressionResultMapper
    ) {
        this.speakingImprovedExpressionResultMapper = speakingImprovedExpressionResultMapper;
    }

    public SpeakingSessionAssessmentResult domainToResult(SpeakingSessionAssessment domain) {
        if (domain == null) {
            return null;
        }

        List<SpeakingImprovedExpressionResult> improvedExpressionResults = null;
        if (domain.getSpeakingImprovedExpressions() != null) {
            improvedExpressionResults = domain.getSpeakingImprovedExpressions().stream()
                    .map(speakingImprovedExpressionResultMapper::domainToResult)
                    .toList();
        }

        return SpeakingSessionAssessmentResult.builder()
                .id(domain.getId())
                .speakingSessionId(domain.getSpeakingSessionId())
                .overallScore(domain.getOverallScore())
                .jlptEstimate(domain.getJlptEstimate())
                .fluencyScore(domain.getFluencyScore())
                .pronunciationScore(domain.getPronunciationScore())
                .grammarScore(domain.getGrammarScore())
                .vocabularyScore(domain.getVocabularyScore())
                .interactionScore(domain.getInteractionScore())
                .naturalnessScore(domain.getNaturalnessScore())
                .coherenceScore(domain.getCoherenceScore())
                .summary(domain.getSummary())
                .strengths(domain.getStrengths())
                .weaknesses(domain.getWeaknesses())
                .feedbackFluency(domain.getFeedbackFluency())
                .feedbackPronunciation(domain.getFeedbackPronunciation())
                .feedbackGrammar(domain.getFeedbackGrammar())
                .feedbackVocabulary(domain.getFeedbackVocabulary())
                .feedbackInteraction(domain.getFeedbackInteraction())
                .feedbackNaturalness(domain.getFeedbackNaturalness())
                .feedbackCoherence(domain.getFeedbackCoherence())
                .studyFocusArea(domain.getStudyFocusArea())
                .studyRecommendation(domain.getStudyRecommendation())
                .studyEncouragement(domain.getStudyEncouragement())
                .speakingImprovedExpressions(improvedExpressionResults)
                .build();
    }
}
