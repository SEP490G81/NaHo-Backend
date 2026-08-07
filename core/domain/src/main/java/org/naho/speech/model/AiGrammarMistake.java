package org.naho.speech.model;

public class AiGrammarMistake {
    private final Long id;
    private final Long grammarFeedbackId;
    private final String original;
    private final String corrected;
    private final String explanationVi;

    private AiGrammarMistake(Builder builder) {
        this.id = builder.id;
        this.grammarFeedbackId = builder.grammarFeedbackId;
        this.original = builder.original;
        this.corrected = builder.corrected;
        this.explanationVi = builder.explanationVi;
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

    public String getOriginal() {
        return original;
    }

    public String getCorrected() {
        return corrected;
    }

    public String getExplanationVi() {
        return explanationVi;
    }

    public static class Builder {
        private Long id;
        private Long grammarFeedbackId;
        private String original;
        private String corrected;
        private String explanationVi;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder grammarFeedbackId(Long grammarFeedbackId) {
            this.grammarFeedbackId = grammarFeedbackId;
            return this;
        }

        public Builder original(String original) {
            this.original = original;
            return this;
        }

        public Builder corrected(String corrected) {
            this.corrected = corrected;
            return this;
        }

        public Builder explanationVi(String explanationVi) {
            this.explanationVi = explanationVi;
            return this;
        }

        public AiGrammarMistake build() {
            return new AiGrammarMistake(this);
        }
    }
}
