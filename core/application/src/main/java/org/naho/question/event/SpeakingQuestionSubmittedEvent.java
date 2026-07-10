package org.naho.question.event;

public record SpeakingQuestionSubmittedEvent(
        Long speakingQuestionId,
        Long userId
) {
}
