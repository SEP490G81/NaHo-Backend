package org.naho.speech.llm.dto.request;

public record SpeakingHistoryFilterRequest(
        Long speakingQuestionId,
        Long topicId,
        String search
) {
}
