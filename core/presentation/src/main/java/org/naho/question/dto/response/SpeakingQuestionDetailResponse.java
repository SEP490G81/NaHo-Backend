package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

public record SpeakingQuestionDetailResponse(
        Long id,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        QuestionStatus status
) {
}
