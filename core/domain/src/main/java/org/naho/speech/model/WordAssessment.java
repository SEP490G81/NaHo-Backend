package org.naho.speech.model;

public class WordAssessment {
    private String word;
    private Double accuracyScore;
    private String errorType; // Omission, Insertion, Mispronunciation, None

    public WordAssessment(String word, Double accuracyScore, String errorType) {
        this.word = word;
        this.accuracyScore = accuracyScore;
        this.errorType = errorType;
    }

    // Getters
    public String getWord() { return word; }
    public Double getAccuracyScore() { return accuracyScore; }
    public String getErrorType() { return errorType; }
}