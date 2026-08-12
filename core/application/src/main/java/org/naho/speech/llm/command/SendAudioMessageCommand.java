package org.naho.speech.llm.command;

import org.naho.file.result.StoredFile;
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
 * @param storedFile    file lưu tạm local (optional)
 * @param userId        ID của user
 */
public record SendAudioMessageCommand(
        String sessionId,
        byte[] audioBytes,
        String referenceText,
        StoredFile storedFile,
        Long userId
) {
    public SendAudioMessageCommand(String sessionId, byte[] audioBytes, String referenceText) {
        this(sessionId, audioBytes, referenceText, null, null);
    }

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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sessionId;
        private byte[] audioBytes;
        private String referenceText;
        private StoredFile storedFile;
        private Long userId;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder audioBytes(byte[] audioBytes) {
            this.audioBytes = audioBytes;
            return this;
        }

        public Builder referenceText(String referenceText) {
            this.referenceText = referenceText;
            return this;
        }

        public Builder storedFile(StoredFile storedFile) {
            this.storedFile = storedFile;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SendAudioMessageCommand build() {
            return new SendAudioMessageCommand(sessionId, audioBytes, referenceText, storedFile, userId);
        }
    }
}
