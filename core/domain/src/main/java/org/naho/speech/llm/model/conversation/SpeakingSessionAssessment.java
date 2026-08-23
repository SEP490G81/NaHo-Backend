package org.naho.speech.llm.model.conversation;

import java.util.List;

public class SpeakingSessionAssessment {

    private Long id;
    private Long speakingSessionId;

    private Integer overallScore;
    private String jlptEstimate;
    private Integer fluencyScore;
    private Integer pronunciationScore;
    private Integer grammarScore;
    private Integer vocabularyScore;
    private Integer interactionScore;
    private Integer naturalnessScore;
    private Integer coherenceScore;

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

    private List<SpeakingImprovedExpression> speakingImprovedExpressions;

    private SpeakingSessionAssessment(Builder builder) {
        this.id = builder.id;
        this.speakingSessionId = builder.speakingSessionId;

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
        this.speakingImprovedExpressions = builder.speakingImprovedExpressions;
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

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getJlptEstimate() {
        return jlptEstimate;
    }

    public void setJlptEstimate(String jlptEstimate) {
        this.jlptEstimate = jlptEstimate;
    }

    public Integer getFluencyScore() {
        return fluencyScore;
    }

    public void setFluencyScore(Integer fluencyScore) {
        this.fluencyScore = fluencyScore;
    }

    public Integer getPronunciationScore() {
        return pronunciationScore;
    }

    public void setPronunciationScore(Integer pronunciationScore) {
        this.pronunciationScore = pronunciationScore;
    }

    public Integer getGrammarScore() {
        return grammarScore;
    }

    public void setGrammarScore(Integer grammarScore) {
        this.grammarScore = grammarScore;
    }

    public Integer getVocabularyScore() {
        return vocabularyScore;
    }

    public void setVocabularyScore(Integer vocabularyScore) {
        this.vocabularyScore = vocabularyScore;
    }

    public Integer getInteractionScore() {
        return interactionScore;
    }

    public void setInteractionScore(Integer interactionScore) {
        this.interactionScore = interactionScore;
    }

    public Integer getNaturalnessScore() {
        return naturalnessScore;
    }

    public void setNaturalnessScore(Integer naturalnessScore) {
        this.naturalnessScore = naturalnessScore;
    }

    public Integer getCoherenceScore() {
        return coherenceScore;
    }

    public void setCoherenceScore(Integer coherenceScore) {
        this.coherenceScore = coherenceScore;
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

    public List<SpeakingImprovedExpression> getSpeakingImprovedExpressions() {
        return speakingImprovedExpressions;
    }

    public void setSpeakingImprovedExpressions(List<SpeakingImprovedExpression> speakingImprovedExpressions) {
        this.speakingImprovedExpressions = speakingImprovedExpressions;
    }

    public static class Builder {

        private Long id;
        private Long speakingSessionId;

        private Integer overallScore;
        private String jlptEstimate;
        private Integer fluencyScore;
        private Integer pronunciationScore;
        private Integer grammarScore;
        private Integer vocabularyScore;
        private Integer interactionScore;
        private Integer naturalnessScore;
        private Integer coherenceScore;

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
        private List<SpeakingImprovedExpression> speakingImprovedExpressions;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speakingSessionId(Long speakingSessionId) {
            this.speakingSessionId = speakingSessionId;
            return this;
        }

        public Builder overallScore(Integer overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public Builder jlptEstimate(String jlptEstimate) {
            this.jlptEstimate = jlptEstimate;
            return this;
        }

        public Builder fluencyScore(Integer fluencyScore) {
            this.fluencyScore = fluencyScore;
            return this;
        }

        public Builder pronunciationScore(Integer pronunciationScore) {
            this.pronunciationScore = pronunciationScore;
            return this;
        }

        public Builder grammarScore(Integer grammarScore) {
            this.grammarScore = grammarScore;
            return this;
        }

        public Builder vocabularyScore(Integer vocabularyScore) {
            this.vocabularyScore = vocabularyScore;
            return this;
        }

        public Builder interactionScore(Integer interactionScore) {
            this.interactionScore = interactionScore;
            return this;
        }

        public Builder naturalnessScore(Integer naturalnessScore) {
            this.naturalnessScore = naturalnessScore;
            return this;
        }

        public Builder coherenceScore(Integer coherenceScore) {
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

        public Builder speakingImprovedExpressions(List<SpeakingImprovedExpression> speakingImprovedExpressions) {
            this.speakingImprovedExpressions = speakingImprovedExpressions;
            return this;
        }

        public SpeakingSessionAssessment build() {
            return new SpeakingSessionAssessment(this);
        }
    }
}
