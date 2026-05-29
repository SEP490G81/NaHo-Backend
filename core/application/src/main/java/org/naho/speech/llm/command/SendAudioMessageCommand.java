package org.naho.speech.llm.command;

import org.naho.speech.llm.constant.AiApplicationMessageKey;
import org.naho.speech.llm.exception.AiApplicationError;
import org.naho.shared.exception.ApplicationException;

/**
 * Command DTO: Gửi audio message trong speaking session.
 *
 * @param sessionId     ID phiên hội thoại
 * @param audioBytes    dữ liệu audio (WAV)
 * @param referenceText text chuẩn để đánh giá phát âm (optional)
 */
public record SendAudioMessageCommand(
        String sessionId,
        byte[] audioBytes,
        String referenceText
) {
    public SendAudioMessageCommand {
        if (sessionId == null || sessionId.isBlank()) {
            throw new ApplicationException(
                    AiApplicationError.AI_SESSION_ID_INVALID,
                    AiApplicationMessageKey.AI_SESSION_ID_INVALID_TITLE
            );
        }
        if (audioBytes == null || audioBytes.length == 0) {
            throw new ApplicationException(
                    AiApplicationError.AI_USER_MESSAGE_INVALID,
                    "Audio data must not be empty"
            );
        }
    }
}
