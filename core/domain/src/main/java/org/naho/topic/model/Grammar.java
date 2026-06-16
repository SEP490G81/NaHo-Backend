package org.naho.topic.model;

public class Grammar {
    private final Long id;
    private final String vietnameseMeaningText;
    private final String englishMeaningText;
    private final String explanation;

    private Grammar(Builder builder) {
        this.id = builder.id;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
        this.explanation = builder.explanation;
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

    public String getExplanation() {
        return explanation;
    }

    public static class Builder {
        private Long id;
        private String vietnameseMeaningText;
        private String englishMeaningText;
        private String explanation;

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

        public Builder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public Grammar build() {
            return new Grammar(this);
        }
    }
}
