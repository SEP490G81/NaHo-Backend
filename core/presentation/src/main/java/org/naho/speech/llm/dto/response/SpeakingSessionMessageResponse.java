package org.naho.speech.llm.dto.response;

import org.naho.speech.llm.type.MessageType;

public record SpeakingSessionMessageResponse(
        Long id,
        Long sessionId,
        Long audioFileId,
        int turnIndex,
        String senderType,
        MessageType messageType,
        String content,
        String contentTranslation,
        String correctedText,
        String correctionExplanation,
        String grammarNote,
        String hintForLearner,
        Double pronunciationScore,
        String aiReplyAudio,
        String userRecordAudio
) {
}
