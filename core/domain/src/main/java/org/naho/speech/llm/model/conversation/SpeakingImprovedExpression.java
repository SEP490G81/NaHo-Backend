package org.naho.speech.llm.model.conversation;

public class SpeakingImprovedExpression {

    private Long id;
    private Long speakingSessionAssessmentId;
    private Integer turnIndex;
    private String originalText;
    private String improvedText;
    private String explanationVietnamese;

    private SpeakingImprovedExpression(Builder builder) {
        this.id = builder.id;
        this.speakingSessionAssessmentId = builder.speakingSessionAssessmentId;
        this.turnIndex = builder.turnIndex;
        this.originalText = builder.originalText;
        this.improvedText = builder.improvedText;
        this.explanationVietnamese = builder.explanationVietnamese;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long speakingSessionAssessmentId;
        private Integer turnIndex;
        private String originalText;
        private String improvedText;
        private String explanationVietnamese;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speakingSessionAssessmentId(Long speakingSessionAssessmentId) {
            this.speakingSessionAssessmentId = speakingSessionAssessmentId;
            return this;
        }

        public Builder turnIndex(Integer turnIndex) {
            this.turnIndex = turnIndex;
            return this;
        }

        public Builder originalText(String originalText) {
            this.originalText = originalText;
            return this;
        }

        public Builder improvedText(String improvedText) {
            this.improvedText = improvedText;
            return this;
        }

        public Builder explanationVietnamese(String explanationVietnamese) {
            this.explanationVietnamese = explanationVietnamese;
            return this;
        }

        public SpeakingImprovedExpression build() {
            return new SpeakingImprovedExpression(this);
        }
    }
}
