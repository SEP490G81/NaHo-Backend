package org.naho.speech.llm.model.conversation;

public class SpeakingSessionAssessment {

    private Long id;
    private Long sessionId;

    private int overallScore;
    private String jlptEstimate;
    private int fluencyScore;
    private int pronunciationScore;
    private int grammarScore;
    private int vocabularyScore;
    private int interactionScore;
    private int naturalnessScore;
    private int coherenceScore;

    private String summary;
    private String strengths;
    private String weaknesses;

    private String feedbackFluency;
    private String feedbackPronunciation;
    private String feedbackGrammar;
    private String feedbackVocabulary;
    private String feedbackInteraction;
    private String feedbackNaturalness;
    private String feedbackCoherence;

    private String studyFocusArea;
    private String studyRecommendation;
    private String studyEncouragement;

    private SpeakingSessionAssessment(Builder builder) {
        this.id = builder.id;
        this.sessionId = builder.sessionId;

        this.overallScore = builder.overallScore;
        this.jlptEstimate = builder.jlptEstimate;
        this.fluencyScore = builder.fluencyScore;
        this.pronunciationScore = builder.pronunciationScore;
        this.grammarScore = builder.grammarScore;
        this.vocabularyScore = builder.vocabularyScore;
        this.interactionScore = builder.interactionScore;
        this.naturalnessScore = builder.naturalnessScore;
        this.coherenceScore = builder.coherenceScore;

        this.summary = builder.summary;
        this.strengths = builder.strengths;
        this.weaknesses = builder.weaknesses;

        this.feedbackFluency = builder.feedbackFluency;
        this.feedbackPronunciation = builder.feedbackPronunciation;
        this.feedbackGrammar = builder.feedbackGrammar;
        this.feedbackVocabulary = builder.feedbackVocabulary;
        this.feedbackInteraction = builder.feedbackInteraction;
        this.feedbackNaturalness = builder.feedbackNaturalness;
        this.feedbackCoherence = builder.feedbackCoherence;

        this.studyFocusArea = builder.studyFocusArea;
        this.studyRecommendation = builder.studyRecommendation;
        this.studyEncouragement = builder.studyEncouragement;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long sessionId;

        private int overallScore;
        private String jlptEstimate;
        private int fluencyScore;
        private int pronunciationScore;
        private int grammarScore;
        private int vocabularyScore;
        private int interactionScore;
        private int naturalnessScore;
        private int coherenceScore;

        private String summary;
        private String strengths;
        private String weaknesses;

        private String feedbackFluency;
        private String feedbackPronunciation;
        private String feedbackGrammar;
        private String feedbackVocabulary;
        private String feedbackInteraction;
        private String feedbackNaturalness;
        private String feedbackCoherence;

        private String studyFocusArea;
        private String studyRecommendation;
        private String studyEncouragement;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sessionId(Long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder overallScore(int overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public Builder jlptEstimate(String jlptEstimate) {
            this.jlptEstimate = jlptEstimate;
            return this;
        }

        public Builder fluencyScore(int fluencyScore) {
            this.fluencyScore = fluencyScore;
            return this;
        }

        public Builder pronunciationScore(int pronunciationScore) {
            this.pronunciationScore = pronunciationScore;
            return this;
        }

        public Builder grammarScore(int grammarScore) {
            this.grammarScore = grammarScore;
            return this;
        }

        public Builder vocabularyScore(int vocabularyScore) {
            this.vocabularyScore = vocabularyScore;
            return this;
        }

        public Builder interactionScore(int interactionScore) {
            this.interactionScore = interactionScore;
            return this;
        }

        public Builder naturalnessScore(int naturalnessScore) {
            this.naturalnessScore = naturalnessScore;
            return this;
        }

        public Builder coherenceScore(int coherenceScore) {
            this.coherenceScore = coherenceScore;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder strengths(String strengths) {
            this.strengths = strengths;
            return this;
        }

        public Builder weaknesses(String weaknesses) {
            this.weaknesses = weaknesses;
            return this;
        }

        public Builder feedbackFluency(String feedbackFluency) {
            this.feedbackFluency = feedbackFluency;
            return this;
        }

        public Builder feedbackPronunciation(String feedbackPronunciation) {
            this.feedbackPronunciation = feedbackPronunciation;
            return this;
        }

        public Builder feedbackGrammar(String feedbackGrammar) {
            this.feedbackGrammar = feedbackGrammar;
            return this;
        }

        public Builder feedbackVocabulary(String feedbackVocabulary) {
            this.feedbackVocabulary = feedbackVocabulary;
            return this;
        }

        public Builder feedbackInteraction(String feedbackInteraction) {
            this.feedbackInteraction = feedbackInteraction;
            return this;
        }

        public Builder feedbackNaturalness(String feedbackNaturalness) {
            this.feedbackNaturalness = feedbackNaturalness;
            return this;
        }

        public Builder feedbackCoherence(String feedbackCoherence) {
            this.feedbackCoherence = feedbackCoherence;
            return this;
        }

        public Builder studyFocusArea(String studyFocusArea) {
            this.studyFocusArea = studyFocusArea;
            return this;
        }

        public Builder studyRecommendation(String studyRecommendation) {
            this.studyRecommendation = studyRecommendation;
            return this;
        }

        public Builder studyEncouragement(String studyEncouragement) {
            this.studyEncouragement = studyEncouragement;
            return this;
        }

        public SpeakingSessionAssessment build() {
            return new SpeakingSessionAssessment(this);
        }
    }
}
