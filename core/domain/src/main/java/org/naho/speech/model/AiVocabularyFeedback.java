package org.naho.speech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiVocabularyFeedback {
    private final Long id;
    private final Long aiFeedbackId;
    private final Integer score;
    private final String comment;
    private final List<String> usedTargetWords;
    private final List<String> missingTargetWords;
    private final List<AiVocabularySuggestion> suggestions;

    private AiVocabularyFeedback(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.score = builder.score;
        this.comment = builder.comment;
        this.usedTargetWords = builder.usedTargetWords != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.usedTargetWords))
                : Collections.emptyList();
        this.missingTargetWords = builder.missingTargetWords != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.missingTargetWords))
                : Collections.emptyList();
        this.suggestions = builder.suggestions != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.suggestions))
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

    public String getComment() {
        return comment;
    }

    public List<String> getUsedTargetWords() {
        return usedTargetWords;
    }

    public List<String> getMissingTargetWords() {
        return missingTargetWords;
    }

    public List<AiVocabularySuggestion> getSuggestions() {
        return suggestions;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private Integer score;
        private String comment;
        private List<String> usedTargetWords;
        private List<String> missingTargetWords;
        private List<AiVocabularySuggestion> suggestions;

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

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder usedTargetWords(List<String> usedTargetWords) {
            this.usedTargetWords = usedTargetWords;
            return this;
        }

        public Builder missingTargetWords(List<String> missingTargetWords) {
            this.missingTargetWords = missingTargetWords;
            return this;
        }

        public Builder suggestions(List<AiVocabularySuggestion> suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        public AiVocabularyFeedback build() {
            return new AiVocabularyFeedback(this);
        }
    }
}
