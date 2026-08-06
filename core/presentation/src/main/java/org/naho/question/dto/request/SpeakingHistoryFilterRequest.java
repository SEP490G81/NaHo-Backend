package org.naho.question.dto.request;

public record SpeakingHistoryFilterRequest(
        Integer page,
        Integer size,
        Long speakingQuestionId,
        Long topicId,
        String search
) {
}
