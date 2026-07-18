package org.naho.question.model;

public class Grammar {
    private final Long id;
    private final String reading;
    private final String japanese;
    private final String vietnameseMeaningText;
    private final String englishMeaningText;

    private Grammar(Builder builder) {
        this.id = builder.id;
        this.reading = builder.reading;
        this.japanese = builder.japanese;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getReading() {
        return reading;
    }

    public String getJapanese() {
        return japanese;
    }

    public String getVietnameseMeaningText() {
        return vietnameseMeaningText;
    }

    public String getEnglishMeaningText() {
        return englishMeaningText;
    }

    public static class Builder {
        private Long id;
        private String reading;
        private String japanese;
        private String vietnameseMeaningText;
        private String englishMeaningText;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder reading(String reading) {
            this.reading = reading;
            return this;
        }

        public Builder japanese(String japanese) {
            this.japanese = japanese;
            return this;
        }

        public Builder vietnameseMeaningText(String vietnameseMeaningText) {
            this.vietnameseMeaningText = vietnameseMeaningText;
            return this;
        }

        public Builder englishMeaningText(String englishMeaningText) {
            this.englishMeaningText = englishMeaningText;
            return this;
        }

        public Grammar build() {
            return new Grammar(this);
        }
    }
}

