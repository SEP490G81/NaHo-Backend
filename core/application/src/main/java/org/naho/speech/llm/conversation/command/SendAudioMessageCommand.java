package org.naho.speech.llm.conversation.command;

import org.naho.file.result.StoredFile;
import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;

public record SendAudioMessageCommand(
        String sessionCode,
        byte[] audioBytes,
        double duration,
        StoredFile storedFile,
        Long userId
) {
    public SendAudioMessageCommand {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmTitleMessageKey.LLM_SESSION_CODE_INVALID_TITLE
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

    public static class Builder {

        private String sessionCode;
        private byte[] audioBytes;
        private double duration;
        private StoredFile storedFile;
        private Long userId;

        public Builder sessionCode(String sessionCode) {
            this.sessionCode = sessionCode;
            return this;
        }

        public Builder audioBytes(byte[] audioBytes) {
            this.audioBytes = audioBytes;
            return this;
        }

        public Builder duration(double duration) {
            this.duration = duration;
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
            return new SendAudioMessageCommand(
                    sessionCode,
                    audioBytes,
                    duration,
                    storedFile,
                    userId
            );
        }
    }
}
