package org.naho.vocabulary.result;

import java.util.List;

public record VocabulariesOfTopicResult(
        Long topicId,
        List<VocabularyResult> vocabularies
) {
}
