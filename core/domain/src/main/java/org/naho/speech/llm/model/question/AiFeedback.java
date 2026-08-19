package org.naho.speech.llm.model.question;

import java.util.List;

public class AiFeedback {
    private Long id;
    private Double grammarScore;
    private Double vocabularyScore;
    private Double naturalnessScore;
    private Double contentRelevantScore;
    private Double averageScore;
    private String suggestJapaneseAnswer;
    private String suggestAnswerTranslation;
    private List<UsedVocabularyAndGrammar> usedVocabulariesAndGrammars;
    private List<UserAnswerError> userAnswerErrors;

    private AiFeedback(Builder builder) {
        this.id = builder.id;
        this.grammarScore = builder.grammarScore;
        this.vocabularyScore = builder.vocabularyScore;
        this.naturalnessScore = builder.naturalnessScore;
        this.contentRelevantScore = builder.contentRelevantScore;
        this.averageScore = builder.averageScore;
        this.suggestJapaneseAnswer = builder.suggestJapaneseAnswer;
        this.suggestAnswerTranslation = builder.suggestAnswerTranslation;
        this.usedVocabulariesAndGrammars = builder.usedVocabulariesAndGrammars;
        this.userAnswerErrors = builder.userAnswerErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Double getGrammarScore() {
        return grammarScore;
    }

    public Double getVocabularyScore() {
        return vocabularyScore;
    }

    public Double getNaturalnessScore() {
        return naturalnessScore;
    }

    public Double getContentRelevantScore() {
        return contentRelevantScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public String getSuggestJapaneseAnswer() {
        return suggestJapaneseAnswer;
    }

    public String getSuggestAnswerTranslation() {
        return suggestAnswerTranslation;
    }

    public List<UsedVocabularyAndGrammar> getUsedVocabulariesAndGrammars() {
        return usedVocabulariesAndGrammars;
    }

    public List<UserAnswerError> getUserAnswerErrors() {
        return userAnswerErrors;
    }

    public static class Builder {
        private Long id;
        private Double grammarScore;
        private Double vocabularyScore;
        private Double naturalnessScore;
        private Double contentRelevantScore;
        private Double averageScore;
        private String suggestJapaneseAnswer;
        private String suggestAnswerTranslation;
        private List<UsedVocabularyAndGrammar> usedVocabulariesAndGrammars;
        private List<UserAnswerError> userAnswerErrors;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder grammarScore(Double grammarScore) {
            this.grammarScore = grammarScore;
            return this;
        }

        public Builder vocabularyScore(Double vocabularyScore) {
            this.vocabularyScore = vocabularyScore;
            return this;
        }

        public Builder naturalnessScore(Double naturalnessScore) {
            this.naturalnessScore = naturalnessScore;
            return this;
        }

        public Builder contentRelevantScore(Double contentRelevantScore) {
            this.contentRelevantScore = contentRelevantScore;
            return this;
        }

        public Builder averageScore(Double averageScore) {
            this.averageScore = averageScore;
            return this;
        }

        public Builder suggestJapaneseAnswer(String suggestJapaneseAnswer) {
            this.suggestJapaneseAnswer = suggestJapaneseAnswer;
            return this;
        }

        public Builder suggestAnswerTranslation(String suggestAnswerTranslation) {
            this.suggestAnswerTranslation = suggestAnswerTranslation;
            return this;
        }

        public Builder usedVocabulariesAndGrammars(
                List<UsedVocabularyAndGrammar> usedVocabulariesAndGrammars
        ) {
            this.usedVocabulariesAndGrammars = usedVocabulariesAndGrammars;
            return this;
        }

        public Builder userAnswerErrors(List<UserAnswerError> userAnswerErrors) {
            this.userAnswerErrors = userAnswerErrors;
            return this;
        }

        public AiFeedback build() {
            return new AiFeedback(this);
        }
    }
}