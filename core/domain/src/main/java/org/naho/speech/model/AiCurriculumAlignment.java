package org.naho.speech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AiCurriculumAlignment {
    private final Long id;
    private final Long contentRelevanceFeedbackId;
    private final Boolean topicMatched;
    private final Boolean lessonMatched;
    private final Boolean questionAnswered;
    private final Boolean canDoAchieved;
    private final List<String> missingPoints;

    private AiCurriculumAlignment(Builder builder) {
        this.id = builder.id;
        this.contentRelevanceFeedbackId = builder.contentRelevanceFeedbackId;
        this.topicMatched = builder.topicMatched;
        this.lessonMatched = builder.lessonMatched;
        this.questionAnswered = builder.questionAnswered;
        this.canDoAchieved = builder.canDoAchieved;
        this.missingPoints = builder.missingPoints != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.missingPoints))
                : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getContentRelevanceFeedbackId() {
        return contentRelevanceFeedbackId;
    }

    public Boolean getTopicMatched() {
        return topicMatched;
    }

    public Boolean getLessonMatched() {
        return lessonMatched;
    }

    public Boolean getQuestionAnswered() {
        return questionAnswered;
    }

    public Boolean getCanDoAchieved() {
        return canDoAchieved;
    }

    public List<String> getMissingPoints() {
        return missingPoints;
    }

    public static class Builder {
        private Long id;
        private Long contentRelevanceFeedbackId;
        private Boolean topicMatched;
        private Boolean lessonMatched;
        private Boolean questionAnswered;
        private Boolean canDoAchieved;
        private List<String> missingPoints;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder contentRelevanceFeedbackId(Long contentRelevanceFeedbackId) {
            this.contentRelevanceFeedbackId = contentRelevanceFeedbackId;
            return this;
        }

        public Builder topicMatched(Boolean topicMatched) {
            this.topicMatched = topicMatched;
            return this;
        }

        public Builder lessonMatched(Boolean lessonMatched) {
            this.lessonMatched = lessonMatched;
            return this;
        }

        public Builder questionAnswered(Boolean questionAnswered) {
            this.questionAnswered = questionAnswered;
            return this;
        }

        public Builder canDoAchieved(Boolean canDoAchieved) {
            this.canDoAchieved = canDoAchieved;
            return this;
        }

        public Builder missingPoints(List<String> missingPoints) {
            this.missingPoints = missingPoints;
            return this;
        }

        public AiCurriculumAlignment build() {
            return new AiCurriculumAlignment(this);
        }
    }
}
