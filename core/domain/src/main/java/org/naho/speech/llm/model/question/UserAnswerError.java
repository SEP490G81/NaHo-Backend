package org.naho.speech.llm.model.question;

public class UserAnswerError {
    private Long id;
    private Long aiFeedbackId;
    private String incorrect;
    private String correction;

    private UserAnswerError(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.incorrect = builder.incorrect;
        this.correction = builder.correction;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getAiFeedbackId() {
        return aiFeedbackId;
    }

    public String getIncorrect() {
        return incorrect;
    }

    public String getCorrection() {
        return correction;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private String incorrect;
        private String correction;

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

        public UserAnswerError build() {
            return new UserAnswerError(this);
        }
    }
}