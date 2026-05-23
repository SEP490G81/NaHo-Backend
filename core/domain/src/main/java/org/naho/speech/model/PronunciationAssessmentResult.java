package org.naho.speech.model;

import java.util.List;

public class PronunciationAssessmentResult {
    private String transcript;              // Đoạn text nhận diện được (Speech To Text)
    private Double accuracyScore;          // Điểm độ chuẩn xác phát âm
    private Double fluencyScore;           // Điểm độ trôi chảy
    private Double completenessScore;      // Điểm độ hoàn thành (chỉ có khi chạy scripted)
    private Double pronunciationScore;     // Điểm đánh giá phát âm tổng thể (PronScore)
    private List<WordAssessment> words;    // Đánh giá chi tiết từng từ

    private PronunciationAssessmentResult(Builder builder) {
        this.transcript = builder.transcript;
        this.accuracyScore = builder.accuracyScore;
        this.fluencyScore = builder.fluencyScore;
        this.completenessScore = builder.completenessScore;
        this.pronunciationScore = builder.pronunciationScore;
        this.words = builder.words;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTranscript() { return transcript; }
    public Double getAccuracyScore() { return accuracyScore; }
    public Double getFluencyScore() { return fluencyScore; }
    public Double getCompletenessScore() { return completenessScore; }
    public Double getPronunciationScore() { return pronunciationScore; }
    public List<WordAssessment> getWords() { return words; }

    public static class Builder {
        private String transcript;
        private Double accuracyScore;
        private Double fluencyScore;
        private Double completenessScore;
        private Double pronunciationScore;
        private List<WordAssessment> words;

        public Builder transcript(String transcript) {
            this.transcript = transcript;
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

        public Builder words(List<WordAssessment> words) {
            this.words = words;
            return this;
        }

        public PronunciationAssessmentResult build() {
            return new PronunciationAssessmentResult(this);
        }
    }
}