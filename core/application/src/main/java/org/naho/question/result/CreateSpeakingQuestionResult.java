package org.naho.question.result;

import org.naho.question.type.QuestionStatus;

public record CreateSpeakingQuestionResult(
        Long id,
        Long userId,
        Long speakingQuestionAudioFileId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        String sampleAnswer,
        QuestionStatus status
) {
}
