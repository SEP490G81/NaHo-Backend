package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record CreateQuestionResult(
        Long id,
        Long objectiveId,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        Double orderIndex,
        QuestionStatus status
) {
}

