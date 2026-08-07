package org.naho.speech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiNextPracticeRecommendation {
    private final Long id;
    private final Long overallFeedbackId;
    private final String reason;
    private final List<String> recommendedGrammar;
    private final List<String> recommendedVocabulary;
    private final String recommendedTopic;

    private AiNextPracticeRecommendation(Builder builder) {
        this.id = builder.id;
        this.overallFeedbackId = builder.overallFeedbackId;
        this.reason = builder.reason;
        this.recommendedGrammar = builder.recommendedGrammar != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.recommendedGrammar))
                : Collections.emptyList();
        this.recommendedVocabulary = builder.recommendedVocabulary != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.recommendedVocabulary))
                : Collections.emptyList();
        this.recommendedTopic = builder.recommendedTopic;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getOverallFeedbackId() {
        return overallFeedbackId;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getRecommendedGrammar() {
        return recommendedGrammar;
    }

    public List<String> getRecommendedVocabulary() {
        return recommendedVocabulary;
    }

    public String getRecommendedTopic() {
        return recommendedTopic;
    }

    public static class Builder {
        private Long id;
        private Long overallFeedbackId;
        private String reason;
        private List<String> recommendedGrammar;
        private List<String> recommendedVocabulary;
        private String recommendedTopic;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder overallFeedbackId(Long overallFeedbackId) {
            this.overallFeedbackId = overallFeedbackId;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder recommendedGrammar(List<String> recommendedGrammar) {
            this.recommendedGrammar = recommendedGrammar;
            return this;
        }

        public Builder recommendedVocabulary(List<String> recommendedVocabulary) {
            this.recommendedVocabulary = recommendedVocabulary;
            return this;
        }

        public Builder recommendedTopic(String recommendedTopic) {
            this.recommendedTopic = recommendedTopic;
            return this;
        }

        public AiNextPracticeRecommendation build() {
            return new AiNextPracticeRecommendation(this);
        }
    }
}
