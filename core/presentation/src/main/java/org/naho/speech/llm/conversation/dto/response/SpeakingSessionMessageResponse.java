package org.naho.speech.llm.conversation.dto.response;

import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SenderType;

public record SpeakingSessionMessageResponse(
        Long id,
        Long sessionId,
        Long audioFileId,
        int turnIndex,
        SenderType senderType,
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
