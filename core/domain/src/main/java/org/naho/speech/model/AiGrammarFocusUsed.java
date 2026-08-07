package org.naho.speech.model;

public class AiGrammarFocusUsed {
    private final Long id;
    private final Long grammarFeedbackId;
    private final String pattern;

    private AiGrammarFocusUsed(Builder builder) {
        this.id = builder.id;
        this.grammarFeedbackId = builder.grammarFeedbackId;
        this.pattern = builder.pattern;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getGrammarFeedbackId() {
        return grammarFeedbackId;
    }

    public String getPattern() {
        return pattern;
    }

    public static class Builder {
        private Long id;
        private Long grammarFeedbackId;
        private String pattern;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder grammarFeedbackId(Long grammarFeedbackId) {
            this.grammarFeedbackId = grammarFeedbackId;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public AiGrammarFocusUsed build() {
            return new AiGrammarFocusUsed(this);
        }
    }
}
