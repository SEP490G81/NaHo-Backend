package org.naho.learning.result;

import org.naho.question.type.QuestionStatus;

public record SpeakingQuestionDetailResult(
        Long id,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        QuestionStatus status
) {
}
