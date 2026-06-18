package org.naho.question.model;

public class Vocabulary {
    private final Long id;
    private final String vietnameseMeaningText;
    private final String englishMeaningText;

    private Vocabulary(Builder builder) {
        this.id = builder.id;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getVietnameseMeaningText() {
        return vietnameseMeaningText;
    }

    public String getEnglishMeaningText() {
        return englishMeaningText;
    }

    public static class Builder {
        private Long id;
        private String vietnameseMeaningText;
        private String englishMeaningText;

        public Builder id(Long id) {
            this.id = id;
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

        public Vocabulary build() {
            return new Vocabulary(this);
        }
    }
}
