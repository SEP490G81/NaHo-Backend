package org.naho.speech.model;

public class AiBetterExpression {
    private final Long id;
    private final Long naturalnessFeedbackId;
    private final String original;
    private final String natural;
    private final String naturalReading;
    private final String explanationVi;

    private AiBetterExpression(Builder builder) {
        this.id = builder.id;
        this.naturalnessFeedbackId = builder.naturalnessFeedbackId;
        this.original = builder.original;
        this.natural = builder.natural;
        this.naturalReading = builder.naturalReading;
        this.explanationVi = builder.explanationVi;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getNaturalnessFeedbackId() {
        return naturalnessFeedbackId;
    }

    public String getOriginal() {
        return original;
    }

    public String getNatural() {
        return natural;
    }

    public String getNaturalReading() {
        return naturalReading;
    }

    public String getReading() {
        return naturalReading;
    }

    public String getExplanationVi() {
        return explanationVi;
    }

    public static class Builder {
        private Long id;
        private Long naturalnessFeedbackId;
        private String original;
        private String natural;
        private String naturalReading;
        private String explanationVi;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder naturalnessFeedbackId(Long naturalnessFeedbackId) {
            this.naturalnessFeedbackId = naturalnessFeedbackId;
            return this;
        }

        public Builder original(String original) {
            this.original = original;
            return this;
        }

        public Builder natural(String natural) {
            this.natural = natural;
            return this;
        }

        public Builder naturalReading(String naturalReading) {
            this.naturalReading = naturalReading;
            return this;
        }

        public Builder reading(String reading) {
            this.naturalReading = reading;
            return this;
        }

        public Builder explanationVi(String explanationVi) {
            this.explanationVi = explanationVi;
            return this;
        }

        public AiBetterExpression build() {
            return new AiBetterExpression(this);
        }
    }
}
