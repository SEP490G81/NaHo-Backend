package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record QuestionListItemResult(
        Long id,
        Long userId,
        Long questionAudioFileId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        Double orderIndex,
        QuestionStatus status
) {
}

