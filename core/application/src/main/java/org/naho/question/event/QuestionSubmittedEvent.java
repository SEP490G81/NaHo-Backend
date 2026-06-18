package org.naho.question.event;

public record QuestionSubmittedEvent(
        Long questionId,
        Long userId
) {
}
