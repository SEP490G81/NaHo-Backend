package org.naho.speech.llm.conversation.result;

import java.util.List;

public record SpeakingSessionAssessmentResult(
        Long id,
        Long speakingSessionId,

        int overallScore,
        String jlptEstimate,
        int fluencyScore,
        int pronunciationScore,
        int grammarScore,
        int vocabularyScore,
        int interactionScore,
        int naturalnessScore,
        int coherenceScore,

        String summary,
        String strengths,
        String weaknesses,

        String feedbackFluency,
        String feedbackPronunciation,
        String feedbackGrammar,
        String feedbackVocabulary,
        String feedbackInteraction,
        String feedbackNaturalness,
        String feedbackCoherence,

        String studyFocusArea,
        String studyRecommendation,
        String studyEncouragement,

        List<SpeakingImprovedExpressionResult> speakingImprovedExpressions
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long speakingSessionId;

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

        private List<SpeakingImprovedExpressionResult> speakingImprovedExpressions;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speakingSessionId(Long speakingSessionId) {
            this.speakingSessionId = speakingSessionId;
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

        public Builder speakingImprovedExpressions(List<SpeakingImprovedExpressionResult> speakingImprovedExpressions) {
            this.speakingImprovedExpressions = speakingImprovedExpressions;
            return this;
        }

        public SpeakingSessionAssessmentResult build() {
            return new SpeakingSessionAssessmentResult(
                    id,
                    speakingSessionId,
                    overallScore,
                    jlptEstimate,
                    fluencyScore,
                    pronunciationScore,
                    grammarScore,
                    vocabularyScore,
                    interactionScore,
                    naturalnessScore,
                    coherenceScore,
                    summary,
                    strengths,
                    weaknesses,
                    feedbackFluency,
                    feedbackPronunciation,
                    feedbackGrammar,
                    feedbackVocabulary,
                    feedbackInteraction,
                    feedbackNaturalness,
                    feedbackCoherence,
                    studyFocusArea,
                    studyRecommendation,
                    studyEncouragement,
                    speakingImprovedExpressions
            );
        }
    }
}
