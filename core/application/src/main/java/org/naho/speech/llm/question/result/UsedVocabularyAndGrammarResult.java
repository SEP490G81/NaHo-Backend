package org.naho.speech.llm.question.result;

import org.naho.speech.llm.type.LanguageCategory;

public record UsedVocabularyAndGrammarResult(
        Long id,
        Long aiFeedbackId,
        String expression,
        LanguageCategory category
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long aiFeedbackId;
        private String expression;
        private LanguageCategory category;

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

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder category(LanguageCategory category) {
            this.category = category;
            return this;
        }

        public UsedVocabularyAndGrammarResult build() {
            return new UsedVocabularyAndGrammarResult(
                    id,
                    aiFeedbackId,
                    expression,
                    category
            );
        }
    }
}
