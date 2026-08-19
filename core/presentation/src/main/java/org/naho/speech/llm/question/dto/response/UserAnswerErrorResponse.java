package org.naho.speech.llm.question.dto.response;

public record UserAnswerErrorResponse(
        Long id,
        Long aiFeedbackId,
        String incorrect,
        String correction
) {
}
