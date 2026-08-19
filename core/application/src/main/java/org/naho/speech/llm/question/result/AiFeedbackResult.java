package org.naho.speech.llm.question.result;

import java.util.List;

public record AiFeedbackResult(
        Long id,
        Double grammarScore,
        Double vocabularyScore,
        Double naturalnessScore,
        Double contentRelevantScore,
        Double averageScore,
        String suggestJapaneseAnswer,
        String suggestAnswerTranslation,
        List<UsedVocabularyAndGrammarResult> usedVocabulariesAndGrammars,
        List<UserAnswerErrorResult> userAnswerErrors) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Double grammarScore;
        private Double vocabularyScore;
        private Double naturalnessScore;
        private Double contentRelevantScore;
        private Double averageScore;
        private String suggestJapaneseAnswer;
        private String suggestAnswerTranslation;
        private List<UsedVocabularyAndGrammarResult> usedVocabulariesAndGrammars;
        private List<UserAnswerErrorResult> userAnswerErrors;

        private Builder() {
        }

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

        public Builder usedVocabulariesAndGrammars(List<UsedVocabularyAndGrammarResult> usedVocabulariesAndGrammars) {
            this.usedVocabulariesAndGrammars = usedVocabulariesAndGrammars;
            return this;
        }

        public Builder userAnswerErrors(List<UserAnswerErrorResult> userAnswerErrors) {
            this.userAnswerErrors = userAnswerErrors;
            return this;
        }

        public AiFeedbackResult build() {
            return new AiFeedbackResult(
                    id,
                    grammarScore,
                    vocabularyScore,
                    naturalnessScore,
                    contentRelevantScore,
                    averageScore,
                    suggestJapaneseAnswer,
                    suggestAnswerTranslation,
                    usedVocabulariesAndGrammars,
                    userAnswerErrors);
        }
    }
}
