package org.naho.speech.llm.command;

public record SpeakingAnalysisCommand(
        Long userId,
        Long topicId,
        Long questionId,
        byte[] audioBytes,
        String contentType,
        String originalFilename,
        Integer durationSec
) {
}
