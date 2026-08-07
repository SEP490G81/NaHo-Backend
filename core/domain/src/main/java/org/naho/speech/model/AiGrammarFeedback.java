package org.naho.speech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiGrammarFeedback {
    private final Long id;
    private final Long aiFeedbackId;
    private final Integer score;
    private final Integer bonusApplied;
    private final List<String> grammarFocusUsed;
    private final String comment;
    private final List<AiGrammarMistake> mistakes;

    private AiGrammarFeedback(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.score = builder.score;
        this.bonusApplied = builder.bonusApplied;
        this.grammarFocusUsed = builder.grammarFocusUsed != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.grammarFocusUsed))
                : Collections.emptyList();
        this.comment = builder.comment;
        this.mistakes = builder.mistakes != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.mistakes))
                : Collections.emptyList();
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

    public Integer getScore() {
        return score;
    }

    public Integer getBonusApplied() {
        return bonusApplied;
    }

    public List<String> getGrammarFocusUsed() {
        return grammarFocusUsed;
    }

    public String getComment() {
        return comment;
    }

    public List<AiGrammarMistake> getMistakes() {
        return mistakes;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private Integer score;
        private Integer bonusApplied;
        private List<String> grammarFocusUsed;
        private String comment;
        private List<AiGrammarMistake> mistakes;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder aiFeedbackId(Long aiFeedbackId) {
            this.aiFeedbackId = aiFeedbackId;
            return this;
        }

        public Builder score(Integer score) {
            this.score = score;
            return this;
        }

        public Builder bonusApplied(Integer bonusApplied) {
            this.bonusApplied = bonusApplied;
            return this;
        }

        public Builder grammarFocusUsed(List<String> grammarFocusUsed) {
            this.grammarFocusUsed = grammarFocusUsed;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder mistakes(List<AiGrammarMistake> mistakes) {
            this.mistakes = mistakes;
            return this;
        }

        public AiGrammarFeedback build() {
            return new AiGrammarFeedback(this);
        }
    }
}
