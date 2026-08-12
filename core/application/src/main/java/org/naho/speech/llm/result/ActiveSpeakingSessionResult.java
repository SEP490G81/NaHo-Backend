package org.naho.speech.llm.result;

import java.time.Instant;
import java.util.List;

public record ActiveSpeakingSessionResult(
        Long id,
        String sessionCode,
        Long personaId,
        String topic,
        String marugotoLevel,
        String formalityLevel,
        int totalTurns,
        Instant startedAt,
        List<SessionMessageItem> messages
) {
    public record SessionMessageItem(
            int turnIndex,
            String senderType,
            String content,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            String audioUrl
    ) {
        public SessionMessageItem(
                int turnIndex,
                String senderType,
                String content,
                String correctedText,
                String correctionExplanation,
                String grammarNote,
                String hintForLearner
        ) {
            this(turnIndex, senderType, content, correctedText, correctionExplanation, grammarNote, hintForLearner, null);
        }
    }
}
