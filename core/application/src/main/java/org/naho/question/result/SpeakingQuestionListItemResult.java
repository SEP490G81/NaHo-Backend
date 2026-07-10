package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record SpeakingQuestionListItemResult(
        Long id,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        QuestionStatus status
) {
}
