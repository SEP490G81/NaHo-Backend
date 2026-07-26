package org.naho.vocabulary.model;

import org.naho.question.model.Grammar;

import java.util.List;

public record VocabularyGrammarImportResult(
        List<Vocabulary> vocabularies,
        List<Grammar> grammars
) {
}
