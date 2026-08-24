package org.naho.speech.azure.command;

public record SpeechAssessmentCommand(
        byte[] audioBytes,
        double duration,
        String referenceText,
        Long userId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private byte[] audioBytes;
        private double duration;
        private String referenceText;
        private Long userId;

        public Builder audioBytes(byte[] audioBytes) {
            this.audioBytes = audioBytes;
            return this;
        }

        public Builder duration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder referenceText(String referenceText) {
            this.referenceText = referenceText;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SpeechAssessmentCommand build() {
            return new SpeechAssessmentCommand(
                    audioBytes,
                    duration,
                    referenceText,
                    userId
            );
        }
    }
}

