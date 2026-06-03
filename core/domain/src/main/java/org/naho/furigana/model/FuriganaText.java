package org.naho.furigana.model;

import java.util.List;

public class FuriganaText {
    private final String originalText;
    private final List<FuriganaToken> tokens;
    private final String fullFurigana;

    private FuriganaText(Builder builder) {
        this.originalText = builder.originalText;
        this.tokens = builder.tokens;
        this.fullFurigana = builder.fullFurigana;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getOriginalText() {
        return originalText;
    }

    public List<FuriganaToken> getTokens() {
        return tokens;
    }

    public String getFullFurigana() {
        return fullFurigana;
    }

    public static class Builder {
        private String originalText;
        private List<FuriganaToken> tokens;
        private String fullFurigana;

        public Builder originalText(String originalText) {
            this.originalText = originalText;
            return this;
        }

        public Builder tokens(List<FuriganaToken> tokens) {
            this.tokens = tokens;
            return this;
        }

        public Builder fullFurigana(String fullFurigana) {
            this.fullFurigana = fullFurigana;
            return this;
        }

        public FuriganaText build() {
            return new FuriganaText(this);
        }
    }
}