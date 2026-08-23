package org.naho.speech.llm.conversation.mapper;

import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;

public class SpeakingSessionAssessmentResultMapper {

    public SpeakingSessionAssessmentResultMapper() {
    }

    public SpeakingSessionAssessmentResult domainToResult(SpeakingSessionAssessment domain) {
        if (domain == null) {
            return null;
        }

        return SpeakingSessionAssessmentResult.builder()
                .id(domain.getId())
                .speakingSessionId(domain.getSpeakingSessionId())
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
                .studyReason(domain.getStudyReason())
                .studyRecommendation(domain.getStudyRecommendation())
                .studyEncouragement(domain.getStudyEncouragement())
                .build();
    }
}
