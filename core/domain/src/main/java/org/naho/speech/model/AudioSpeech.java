package org.naho.speech.model;

import org.naho.shared.exception.DomainException;
import org.naho.speech.exception.SpeechErrorCode; // Giả định đã có hoặc tự định nghĩa lỗi Domain

public class AudioSpeech {
    private final byte[] audioData;
    private final String contentType;
    //private final long durationMs; // Độ dài âm thanh (miligiây) - tùy chọn thêm nếu cần

    private AudioSpeech(Builder builder) {
        this.audioData = builder.audioData;
        this.contentType = builder.contentType;
        //this.durationMs = builder.durationMs;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public String getContentType() {
        return contentType;
    }

//    public long getDurationMs() {
//        return durationMs;
//    }

    public static class Builder {
        private byte[] audioData;
        private String contentType = "audio/wav"; // Mặc định là WAV
        private long durationMs;

        public Builder audioData(byte[] audioData) {
            this.audioData = audioData;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public AudioSpeech build() {
            if (audioData == null || audioData.length == 0) {
                // Ví dụ ném exception nghiệp vụ nếu thiếu dữ liệu âm thanh
                throw new DomainException(SpeechErrorCode.AUDIO_DATA_REQUIRED, "Audio data cannot be empty!");
            }
            return new AudioSpeech(this);
        }
    }
}