package org.naho.speech.llm.model.question;

import org.naho.speech.llm.type.LanguageCategory;

public class UsedVocabularyAndGrammar {
    private Long id;
    private Long aiFeedbackId;
    private String expression;
    private LanguageCategory category;

    private UsedVocabularyAndGrammar(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.expression = builder.expression;
        this.category = builder.category;
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

    public String getExpression() {
        return expression;
    }

    public LanguageCategory getCategory() {
        return category;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private String expression;
        private LanguageCategory category;

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

        public UsedVocabularyAndGrammar build() {
            return new UsedVocabularyAndGrammar(this);
        }
    }
}