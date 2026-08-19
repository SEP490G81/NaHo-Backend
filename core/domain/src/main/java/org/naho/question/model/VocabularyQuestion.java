package org.naho.question.model;

import org.naho.vocabulary.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class VocabularyQuestion {

    private final Long id;
    private final List<Vocabulary> vocabularies;

    private VocabularyQuestion(Builder builder) {
        this.id = builder.id;
        this.vocabularies = builder.vocabularies != null ? new ArrayList<>(builder.vocabularies) : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public List<Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void update(List<Vocabulary> vocabularies) {
        if (vocabularies != null) {
            this.vocabularies.clear();
            this.vocabularies.addAll(vocabularies);
        }
    }

    public static final class Builder {

        private Long id;
        private List<Vocabulary> vocabularies;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder vocabularies(List<Vocabulary> vocabularies) {
            this.vocabularies = vocabularies;
            return this;
        }

        public VocabularyQuestion build() {
            return new VocabularyQuestion(this);
        }
    }
}
