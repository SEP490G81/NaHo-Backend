package org.naho.speech.llm.question.mapper;

import org.naho.speech.llm.model.question.UserAnswerError;
import org.naho.speech.llm.question.result.UserAnswerErrorResult;

public class UserAnswerErrorResultMapper {

    public UserAnswerErrorResult domainToResult(UserAnswerError domain) {
        if (domain == null) {
            return null;
        }
        return UserAnswerErrorResult.builder()
                .id(domain.getId())
                .aiFeedbackId(domain.getAiFeedbackId())
                .incorrect(domain.getIncorrect())
                .correction(domain.getCorrection())
                .build();
    }
}
