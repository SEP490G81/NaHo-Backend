package org.naho.speech.model;

public class AiOverallFeedback {
    private final Long id;
    private final Long aiFeedbackId;
    private final Integer languageScore;
    private final AiPronunciationReference pronunciationReference;
    private final String summaryVi;
    private final String correctedAnswerJa;
    private final String correctedAnswerVi;
    private final AiNextPracticeRecommendation nextPracticeRecommendation;

    private AiOverallFeedback(Builder builder) {
        this.id = builder.id;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.languageScore = builder.languageScore;
        this.pronunciationReference = builder.pronunciationReference;
        this.summaryVi = builder.summaryVi;
        this.correctedAnswerJa = builder.correctedAnswerJa;
        this.correctedAnswerVi = builder.correctedAnswerVi;
        this.nextPracticeRecommendation = builder.nextPracticeRecommendation;
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

    public Integer getLanguageScore() {
        return languageScore;
    }

    public AiPronunciationReference getPronunciationReference() {
        return pronunciationReference;
    }

    public String getSummaryVi() {
        return summaryVi;
    }

    public String getCorrectedAnswerJa() {
        return correctedAnswerJa;
    }

    public String getCorrectedAnswerVi() {
        return correctedAnswerVi;
    }

    public AiNextPracticeRecommendation getNextPracticeRecommendation() {
        return nextPracticeRecommendation;
    }

    public static class Builder {
        private Long id;
        private Long aiFeedbackId;
        private Integer languageScore;
        private AiPronunciationReference pronunciationReference;
        private String summaryVi;
        private String correctedAnswerJa;
        private String correctedAnswerVi;
        private AiNextPracticeRecommendation nextPracticeRecommendation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder aiFeedbackId(Long aiFeedbackId) {
            this.aiFeedbackId = aiFeedbackId;
            return this;
        }

        public Builder languageScore(Integer languageScore) {
            this.languageScore = languageScore;
            return this;
        }

        public Builder pronunciationReference(AiPronunciationReference pronunciationReference) {
            this.pronunciationReference = pronunciationReference;
            return this;
        }

        public Builder summaryVi(String summaryVi) {
            this.summaryVi = summaryVi;
            return this;
        }

        public Builder correctedAnswerJa(String correctedAnswerJa) {
            this.correctedAnswerJa = correctedAnswerJa;
            return this;
        }

        public Builder correctedAnswerVi(String correctedAnswerVi) {
            this.correctedAnswerVi = correctedAnswerVi;
            return this;
        }

        public Builder nextPracticeRecommendation(AiNextPracticeRecommendation nextPracticeRecommendation) {
            this.nextPracticeRecommendation = nextPracticeRecommendation;
            return this;
        }

        public AiOverallFeedback build() {
            return new AiOverallFeedback(this);
        }
    }
}
