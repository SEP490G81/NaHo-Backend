package org.naho.speech.azure.model;

import java.util.List;

public class SpeechAssessment {
    private Long id;
    private Long answerHistoryId;
    private String transcriptText;              // Đoạn text nhận diện được (Speech To Text)
    private Double accuracyScore;               // Điểm độ chuẩn xác phát âm
    private Double fluencyScore;                // Điểm độ trôi chảy
    private Double completenessScore;           // Điểm độ hoàn thành
    private Double pronunciationScore;          // Điểm phát âm tổng thể
    private List<WordAssessment> words;         // Đánh giá từng từ

    // Private constructor dùng cho Builder
    private SpeechAssessment(Builder builder) {
        this.id = builder.id;
        this.answerHistoryId = builder.answerHistoryId;
        this.transcriptText = builder.transcriptText;
        this.accuracyScore = builder.accuracyScore;
        this.fluencyScore = builder.fluencyScore;
        this.completenessScore = builder.completenessScore;
        this.pronunciationScore = builder.pronunciationScore;
        this.words = builder.words;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public void setAnswerHistoryId(Long answerHistoryId) {
        this.answerHistoryId = answerHistoryId;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public Double getAccuracyScore() {
        return accuracyScore;
    }

    public Double getFluencyScore() {
        return fluencyScore;
    }

    public Double getCompletenessScore() {
        return completenessScore;
    }

    public Double getPronunciationScore() {
        return pronunciationScore;
    }

    public List<org.naho.speech.azure.model.WordAssessment> getWords() {
        return words;
    }

    // Builder
    public static class Builder {
        private Long id;
        private Long answerHistoryId;
        private String transcriptText;
        private Double accuracyScore;
        private Double fluencyScore;
        private Double completenessScore;
        private Double pronunciationScore;
        private List<org.naho.speech.azure.model.WordAssessment> words;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder transcriptText(String transcriptText) {
            this.transcriptText = transcriptText;
            return this;
        }

        public Builder accuracyScore(Double accuracyScore) {
            this.accuracyScore = accuracyScore;
            return this;
        }

        public Builder fluencyScore(Double fluencyScore) {
            this.fluencyScore = fluencyScore;
            return this;
        }

        public Builder completenessScore(Double completenessScore) {
            this.completenessScore = completenessScore;
            return this;
        }

        public Builder pronunciationScore(Double pronunciationScore) {
            this.pronunciationScore = pronunciationScore;
            return this;
        }

        public Builder words(List<org.naho.speech.azure.model.WordAssessment> words) {
            this.words = words;
            return this;
        }

        public SpeechAssessment build() {
            return new SpeechAssessment(this);
        }
    }
}