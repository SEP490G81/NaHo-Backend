package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record UpdateSpeakingQuestionResult(
        Long id,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        String sampleAnswer,
        QuestionStatus status
) {
}
