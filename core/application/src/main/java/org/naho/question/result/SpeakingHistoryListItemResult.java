package org.naho.question.result;

import java.time.Instant;

public record SpeakingHistoryListItemResult(
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
        Instant practicedAt
) {
}
