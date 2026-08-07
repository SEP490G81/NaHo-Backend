package org.naho.speech.model;

public class AiContentRelevanceFeedback {
    private final Long id;
    private final Long aiFeedbackId;
    private final Integer score;
    private final AiCurriculumAlignment curriculumAlignment;
    private final String comment;

    private AiContentRelevanceFeedback(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.score = builder.score;
        this.curriculumAlignment = builder.curriculumAlignment;
        this.comment = builder.comment;
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

    public AiCurriculumAlignment getCurriculumAlignment() {
        return curriculumAlignment;
    }

    public String getComment() {
        return comment;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private Integer score;
        private AiCurriculumAlignment curriculumAlignment;
        private String comment;

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

        public Builder curriculumAlignment(AiCurriculumAlignment curriculumAlignment) {
            this.curriculumAlignment = curriculumAlignment;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public AiContentRelevanceFeedback build() {
            return new AiContentRelevanceFeedback(this);
        }
    }
}
