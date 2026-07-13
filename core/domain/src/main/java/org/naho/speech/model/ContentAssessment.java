package org.naho.speech.model;

public class ContentAssessment {
    private Long id;
    private Long answerHistoryId;
    private Double vocabularyScore;
    private Double grammarScore;
    private String aiFeedback;
    private String translationText;

    // Private constructor dùng cho Builder
    private ContentAssessment(Builder builder) {
        this.id = builder.id;
        this.answerHistoryId = builder.answerHistoryId;
        this.vocabularyScore = builder.vocabularyScore;
        this.grammarScore = builder.grammarScore;
        this.aiFeedback = builder.aiFeedback;
        this.translationText = builder.translationText;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public Double getVocabularyScore() {
        return vocabularyScore;
    }

    public Double getGrammarScore() {
        return grammarScore;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public String getTranslationText() {
        return translationText;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long answerHistoryId;
        private Double vocabularyScore;
        private Double grammarScore;
        private String aiFeedback;
        private String translationText;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder vocabularyScore(Double vocabularyScore) {
            this.vocabularyScore = vocabularyScore;
            return this;
        }

        public Builder grammarScore(Double grammarScore) {
            this.grammarScore = grammarScore;
            return this;
        }

        public Builder aiFeedback(String aiFeedback) {
            this.aiFeedback = aiFeedback;
            return this;
        }

        public Builder translationText(String translationText) {
            this.translationText = translationText;
            return this;
        }

        public ContentAssessment build() {
            return new ContentAssessment(this);
        }
    }
}