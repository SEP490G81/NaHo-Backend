package org.naho.furigana.model;

import java.util.List;

public class FuriganaText {
    private final String originalText;
    private final List<FuriganaToken> tokens;
    private final String markupString;

    private FuriganaText(Builder builder) {
        this.originalText = builder.originalText;
        this.tokens = builder.tokens;
        this.markupString = builder.markupString;
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

    public String getMarkupString() {
        return markupString;
    }

    public static class Builder {
        private String originalText;
        private List<FuriganaToken> tokens;
        private String markupString;

        public Builder originalText(String originalText) {
            this.originalText = originalText;
            return this;
        }

        public Builder tokens(List<FuriganaToken> tokens) {
            this.tokens = tokens;
            return this;
        }

        public Builder markupString(String markupString) {
            this.markupString = markupString;
            return this;
        }

        public FuriganaText build() {
            return new FuriganaText(this);
        }
    }
}