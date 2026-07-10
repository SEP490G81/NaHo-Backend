package org.naho.question.model;

public class VocabularyQuestion {

    private final Long id;

    private VocabularyQuestion(Builder builder) {
        this.id = builder.id;
    }

    public Long getId() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public VocabularyQuestion build() {
            return new VocabularyQuestion(this);
        }
    }
}
