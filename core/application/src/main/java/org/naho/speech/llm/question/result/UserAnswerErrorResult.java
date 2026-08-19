package org.naho.speech.llm.question.result;

public record UserAnswerErrorResult(
        Long id,
        Long aiFeedbackId,
        String incorrect,
        String correction
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long aiFeedbackId;
        private String incorrect;
        private String correction;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder aiFeedbackId(Long aiFeedbackId) {
            this.aiFeedbackId = aiFeedbackId;
            return this;
        }

        public Builder incorrect(String incorrect) {
            this.incorrect = incorrect;
            return this;
        }

        public Builder correction(String correction) {
            this.correction = correction;
            return this;
        }

        public UserAnswerErrorResult build() {
            return new UserAnswerErrorResult(
                    id,
                    aiFeedbackId,
                    incorrect,
                    correction
            );
        }
    }
}
