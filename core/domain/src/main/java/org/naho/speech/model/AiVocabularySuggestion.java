package org.naho.speech.model;

public class AiVocabularySuggestion {
    private final Long id;
    private final Long vocabularyFeedbackId;
    private final String word;
    private final String reading;
    private final String meaning;
    private final String note;

    private AiVocabularySuggestion(Builder builder) {
        this.id = builder.id;
        this.vocabularyFeedbackId = builder.vocabularyFeedbackId;
        this.word = builder.word;
        this.reading = builder.reading;
        this.meaning = builder.meaning;
        this.note = builder.note;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getVocabularyFeedbackId() {
        return vocabularyFeedbackId;
    }

    public String getWord() {
        return word;
    }

    public String getReading() {
        return reading;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getNote() {
        return note;
    }

    public static class Builder {
        private Long id;
        private Long vocabularyFeedbackId;
        private String word;
        private String reading;
        private String meaning;
        private String note;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder vocabularyFeedbackId(Long vocabularyFeedbackId) {
            this.vocabularyFeedbackId = vocabularyFeedbackId;
            return this;
        }

        public Builder word(String word) {
            this.word = word;
            return this;
        }

        public Builder reading(String reading) {
            this.reading = reading;
            return this;
        }

        public Builder meaning(String meaning) {
            this.meaning = meaning;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public AiVocabularySuggestion build() {
            return new AiVocabularySuggestion(this);
        }
    }
}
