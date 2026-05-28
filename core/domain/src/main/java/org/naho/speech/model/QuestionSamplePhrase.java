package org.naho.speech.model;

import java.util.List;

public class QuestionSamplePhrase {
    private Long id;
    private Long questionId;
    private List<JapaneseTokenizer> tokenizers;
    private String vietnameseMeaningText;
    private String englishMeaningText;

    // Private constructor dùng cho Builder
    private QuestionSamplePhrase(Builder builder) {
        this.id = builder.id;
        this.questionId = builder.questionId;
        this.tokenizers = builder.tokenizers;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public List<JapaneseTokenizer> getTokenizers() {
        return tokenizers;
    }

    public String getVietnameseMeaningText() {
        return vietnameseMeaningText;
    }

    public String getEnglishMeaningText() {
        return englishMeaningText;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long questionId;
        private List<JapaneseTokenizer> tokenizers;
        private String vietnameseMeaningText;
        private String englishMeaningText;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder tokenizers(List<JapaneseTokenizer> tokenizers) {
            this.tokenizers = tokenizers;
            return this;
        }

        public Builder vietnameseMeaningText(String vietnameseMeaningText) {
            this.vietnameseMeaningText = vietnameseMeaningText;
            return this;
        }

        public Builder englishMeaningText(String englishMeaningText) {
            this.englishMeaningText = englishMeaningText;
            return this;
        }

        public QuestionSamplePhrase build() {
            return new QuestionSamplePhrase(this);
        }
    }
}