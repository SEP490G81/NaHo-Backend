package org.naho.furigana.model;

public class FuriganaToken {
    private final String kanji;
    private final String furigana;

    private FuriganaToken(Builder builder) {
        this.kanji = builder.kanji;
        this.furigana = builder.furigana;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getKanji() {
        return kanji;
    }

    public String getFurigana() {
        return furigana;
    }

    public static class Builder {
        private String kanji;
        private String furigana;

        public Builder kanji(String kanji) {
            this.kanji = kanji;
            return this;
        }

        public Builder furigana(String furigana) {
            this.furigana = furigana;
            return this;
        }

        public FuriganaToken build() {
            return new FuriganaToken(this);
        }
    }

}
