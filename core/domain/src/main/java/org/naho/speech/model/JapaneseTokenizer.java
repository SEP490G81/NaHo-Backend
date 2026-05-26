package org.naho.speech.model;

import java.util.Set;

public class JapaneseTokenizer {
    private Long id;
    private String japaneseText;
    private String readingText;
    private Set<QuestionVocabulary> questionVocabularies;
    private Set<QuestionSamplePhrase> questionSamplePhrases;

    // Private constructor dùng cho Builder
    private JapaneseTokenizer(Builder builder) {
        this.id = builder.id;
        this.japaneseText = builder.japaneseText;
        this.readingText = builder.readingText;
        this.questionVocabularies = builder.questionVocabularies;
        this.questionSamplePhrases = builder.questionSamplePhrases;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getJapaneseText() {
        return japaneseText;
    }

    public String getReadingText() {
        return readingText;
    }

    public Set<QuestionVocabulary> getQuestionVocabularies() {
        return questionVocabularies;
    }

    public Set<QuestionSamplePhrase> getQuestionSamplePhrases() {
        return questionSamplePhrases;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    public static class Builder {
        private Long id;
        private String japaneseText;
        private String readingText;
        private Set<QuestionVocabulary> questionVocabularies;
        private Set<QuestionSamplePhrase> questionSamplePhrases;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder japaneseText(String japaneseText) {
            this.japaneseText = japaneseText;
            return this;
        }

        public Builder readingText(String readingText) {
            this.readingText = readingText;
            return this;
        }

        public Builder questionVocabularies(Set<QuestionVocabulary> questionVocabularies) {
            this.questionVocabularies = questionVocabularies;
            return this;
        }

        public Builder questionSamplePhrases(Set<QuestionSamplePhrase> questionSamplePhrases) {
            this.questionSamplePhrases = questionSamplePhrases;
            return this;
        }

        public JapaneseTokenizer build() {
            return new JapaneseTokenizer(this);
        }
    }
}