package org.naho.speech.llm.dto.response;

public record SpeakingHistoryListItemResponse(
        Long historyId,
        Long speakingQuestionId,
        String speakingQuestionTitle,
        Long topicId,
        String topicName,
        Long learningPathNodeId,
        Long bookId,
        Double score,
        Integer durationSec,
        String audioUrl,
        String practicedAt
) {
}
