package org.naho.speech.llm.model.conversation;

public class SpeakingSessionAssessment {

    private Long id;
    private Long speakingSessionId;

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
    private String studyReason;
    private String studyRecommendation;
    private String studyEncouragement;

    private SpeakingSessionAssessment(Builder builder) {
        this.id = builder.id;
        this.speakingSessionId = builder.speakingSessionId;
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
        this.studyReason = builder.studyReason;
        this.studyRecommendation = builder.studyRecommendation;
        this.studyEncouragement = builder.studyEncouragement;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSpeakingSessionId() {
        return speakingSessionId;
    }

    public void setSpeakingSessionId(Long speakingSessionId) {
        this.speakingSessionId = speakingSessionId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getFeedbackFluency() {
        return feedbackFluency;
    }

    public void setFeedbackFluency(String feedbackFluency) {
        this.feedbackFluency = feedbackFluency;
    }

    public String getFeedbackPronunciation() {
        return feedbackPronunciation;
    }

    public void setFeedbackPronunciation(String feedbackPronunciation) {
        this.feedbackPronunciation = feedbackPronunciation;
    }

    public String getFeedbackGrammar() {
        return feedbackGrammar;
    }

    public void setFeedbackGrammar(String feedbackGrammar) {
        this.feedbackGrammar = feedbackGrammar;
    }

    public String getFeedbackVocabulary() {
        return feedbackVocabulary;
    }

    public void setFeedbackVocabulary(String feedbackVocabulary) {
        this.feedbackVocabulary = feedbackVocabulary;
    }

    public String getFeedbackInteraction() {
        return feedbackInteraction;
    }

    public void setFeedbackInteraction(String feedbackInteraction) {
        this.feedbackInteraction = feedbackInteraction;
    }

    public String getFeedbackNaturalness() {
        return feedbackNaturalness;
    }

    public void setFeedbackNaturalness(String feedbackNaturalness) {
        this.feedbackNaturalness = feedbackNaturalness;
    }

    public String getFeedbackCoherence() {
        return feedbackCoherence;
    }

    public void setFeedbackCoherence(String feedbackCoherence) {
        this.feedbackCoherence = feedbackCoherence;
    }

    public String getStudyFocusArea() {
        return studyFocusArea;
    }

    public void setStudyFocusArea(String studyFocusArea) {
        this.studyFocusArea = studyFocusArea;
    }

    public String getStudyReason() {
        return studyReason;
    }

    public void setStudyReason(String studyReason) {
        this.studyReason = studyReason;
    }

    public String getStudyRecommendation() {
        return studyRecommendation;
    }

    public void setStudyRecommendation(String studyRecommendation) {
        this.studyRecommendation = studyRecommendation;
    }

    public String getStudyEncouragement() {
        return studyEncouragement;
    }

    public void setStudyEncouragement(String studyEncouragement) {
        this.studyEncouragement = studyEncouragement;
    }

    public static class Builder {

        private Long id;
        private Long speakingSessionId;

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
        private String studyReason;
        private String studyRecommendation;
        private String studyEncouragement;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speakingSessionId(Long speakingSessionId) {
            this.speakingSessionId = speakingSessionId;
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

        public Builder studyReason(String studyReason) {
            this.studyReason = studyReason;
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
