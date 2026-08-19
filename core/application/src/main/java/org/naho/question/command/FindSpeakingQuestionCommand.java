package org.naho.question.command;

public record FindSpeakingQuestionCommand(
        Long speakingQuestionId,
        Long userId
) {
}
