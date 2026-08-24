package org.naho.speech.llm.conversation.mapper;

import org.naho.speech.llm.conversation.result.SpeakingImprovedExpressionResult;
import org.naho.speech.llm.model.conversation.SpeakingImprovedExpression;

public class SpeakingImprovedExpressionResultMapper {

    public SpeakingImprovedExpressionResult domainToResult(SpeakingImprovedExpression domain) {
        if (domain == null) {
            return null;
        }

        return new SpeakingImprovedExpressionResult(
                domain.getId(),
                domain.getSpeakingSessionAssessmentId(),
                domain.getOriginalText(),
                domain.getImprovedText(),
                domain.getExplanationVietnamese()
        );
    }
}
