package org.naho.speech.llm.dto.request;

public record SpeakingHistoryFilterRequest(
        Integer page,
        Integer size,
        Long speakingQuestionId,
        Long topicId,
        String search
) {
}
