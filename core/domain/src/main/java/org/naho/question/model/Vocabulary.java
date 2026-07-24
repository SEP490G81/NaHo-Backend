package org.naho.question.model;

public class Vocabulary {
    private final Long id;
    private final String kana;
    private final String kanji;
    private final String vietnameseMeaningText;
    private final String englishMeaningText;
    private final Long questionId;

    private Vocabulary(Builder builder) {
        this.id = builder.id;
        this.kana = builder.kana;
        this.kanji = builder.kanji;
        this.vietnameseMeaningText = builder.vietnameseMeaningText;
        this.englishMeaningText = builder.englishMeaningText;
        this.questionId = builder.questionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getKana() {
        return kana;
    }

    public String getKanji() {
        return kanji;
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
        private String kana;
        private String kanji;
        private String vietnameseMeaningText;
        private String englishMeaningText;
        private Long questionId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder kana(String kana) {
            this.kana = kana;
            return this;
        }

        public Builder kanji(String kanji) {
            this.kanji = kanji;
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
