package org.naho.vocabulary.model;

public class Vocabulary {
    private final Long id;
    private final String reading;
    private final String japanese;
    private final String vietnameseMeaningText;
    private final String englishMeaningText;
    private final Long questionId;

    private Vocabulary(Builder builder) {
        this.id = builder.id;
        this.japanese = builder.japanese;
        this.reading = containsKanji(builder.japanese) && builder.reading != null && !builder.reading.trim().isEmpty()
                ? builder.reading.trim()
                : null;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
        this.questionId = builder.questionId;
    }

    private static boolean containsKanji(String s) {
        if (s == null) {
            return false;
        }
        return s.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FAF);
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

    public Long getQuestionId() {
        return questionId;
    }

    public static class Builder {
        private Long id;
        private String reading;
        private String japanese;
        private String vietnameseMeaningText;
        private String englishMeaningText;
        private Long questionId;

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

        public Builder vietnameseMeaningText(String v) {
            this.vietnameseMeaningText = v;
            return this;
        }

        public Builder englishMeaningText(String e) {
            this.englishMeaningText = e;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Vocabulary build() {
            return new Vocabulary(this);
        }
    }
}
