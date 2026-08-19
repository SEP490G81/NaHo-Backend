package org.naho.grammar.result;

public record GrammarResult(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {

    public static Builder builder() {
        return new Builder();
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

        public GrammarResult build() {
            return new GrammarResult(
                    id,
                    reading,
                    japanese,
                    vietnameseMeaningText,
                    englishMeaningText
            );
        }
    }
}