package org.naho.speech.llm.command;

public record SpeakingAnalysisCommand(
        Long userId,
        Long speakingQuestionId,
        byte[] audioBytes,
        String contentType,
        String originalFilename,
        Integer durationSec
) {

}
