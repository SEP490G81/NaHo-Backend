package org.naho.speech.model;

public class AiFeedback {
    private final Long id;
    private final Long answerHistoryId;
    private final Long contentAssessmentId;
    private final AiGrammarFeedback grammar;
    private final AiVocabularyFeedback vocabulary;
    private final AiContentRelevanceFeedback contentRelevance;
    private final AiNaturalnessFeedback naturalness;
    private final AiOverallFeedback overall;

    private AiFeedback(Builder builder) {
        this.id = builder.id;
        this.answerHistoryId = builder.answerHistoryId;
        this.contentAssessmentId = builder.contentAssessmentId;
        this.grammar = builder.grammar;
        this.vocabulary = builder.vocabulary;
        this.contentRelevance = builder.contentRelevance;
        this.naturalness = builder.naturalness;
        this.overall = builder.overall;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public Long getContentAssessmentId() {
        return contentAssessmentId;
    }

    public AiGrammarFeedback getGrammar() {
        return grammar;
    }

    public AiVocabularyFeedback getVocabulary() {
        return vocabulary;
    }

    public AiContentRelevanceFeedback getContentRelevance() {
        return contentRelevance;
    }

    public AiNaturalnessFeedback getNaturalness() {
        return naturalness;
    }

    public AiOverallFeedback getOverall() {
        return overall;
    }

    public static class Builder {
        private Long id;
        private Long answerHistoryId;
        private Long contentAssessmentId;
        private AiGrammarFeedback grammar;
        private AiVocabularyFeedback vocabulary;
        private AiContentRelevanceFeedback contentRelevance;
        private AiNaturalnessFeedback naturalness;
        private AiOverallFeedback overall;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder contentAssessmentId(Long contentAssessmentId) {
            this.contentAssessmentId = contentAssessmentId;
            return this;
        }

        public Builder grammar(AiGrammarFeedback grammar) {
            this.grammar = grammar;
            return this;
        }

        public Builder vocabulary(AiVocabularyFeedback vocabulary) {
            this.vocabulary = vocabulary;
            return this;
        }

        public Builder contentRelevance(AiContentRelevanceFeedback contentRelevance) {
            this.contentRelevance = contentRelevance;
            return this;
        }

        public Builder naturalness(AiNaturalnessFeedback naturalness) {
            this.naturalness = naturalness;
            return this;
        }

        public Builder overall(AiOverallFeedback overall) {
            this.overall = overall;
            return this;
        }

        public AiFeedback build() {
            return new AiFeedback(this);
        }
    }
}
