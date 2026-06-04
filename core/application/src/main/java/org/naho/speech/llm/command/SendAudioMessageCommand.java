package org.naho.speech.llm.command;

import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;

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
                    LlmApplicationError.LLM_SESSION_ID_INVALID,
                    LlmTitleMessageKey.LLM_SESSION_ID_INVALID_TITLE
            );
        }
        if (audioBytes == null || audioBytes.length == 0) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_USER_MESSAGE_INVALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }
    }
}
