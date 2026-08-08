package org.naho.quote.model;

public class Quote {

    private final Long id;
    private final String kanji;
    private final String hiragana;
    private final String romaji;
    private final String translation;
    private final String kanjiDetail;

    private Quote(Builder builder) {
        this.id = builder.id;
        this.kanji = builder.kanji;
        this.hiragana = builder.hiragana;
        this.romaji = builder.romaji;
        this.translation = builder.translation;
        this.kanjiDetail = builder.kanjiDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getKanji() {
        return kanji;
    }

    public String getHiragana() {
        return hiragana;
    }

    public String getRomaji() {
        return romaji;
    }

    public String getTranslation() {
        return translation;
    }

    public String getKanjiDetail() {
        return kanjiDetail;
    }

    public static class Builder {
        private Long id;
        private String kanji;
        private String hiragana;
        private String romaji;
        private String translation;
        private String kanjiDetail;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder kanji(String kanji) {
            this.kanji = kanji;
            return this;
        }

        public Builder hiragana(String hiragana) {
            this.hiragana = hiragana;
            return this;
        }

        public Builder romaji(String romaji) {
            this.romaji = romaji;
            return this;
        }

        public Builder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public Builder kanjiDetail(String kanjiDetail) {
            this.kanjiDetail = kanjiDetail;
            return this;
        }

        public Quote build() {
            return new Quote(this);
        }
    }
}
