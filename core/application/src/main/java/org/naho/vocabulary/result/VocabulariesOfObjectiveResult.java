package org.naho.vocabulary.result;

import java.util.List;

public record VocabulariesOfObjectiveResult(
        Long objectiveId,
        List<VocabularyResult> vocabularies
) {
}
