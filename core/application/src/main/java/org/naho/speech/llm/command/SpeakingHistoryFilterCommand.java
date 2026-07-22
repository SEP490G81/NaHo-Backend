package org.naho.speech.llm.command;

public record SpeakingHistoryFilterCommand(
        Long userId,
        Long speakingQuestionId,
        Long topicId,
        String search,
        int page,
        int size
) {
}
