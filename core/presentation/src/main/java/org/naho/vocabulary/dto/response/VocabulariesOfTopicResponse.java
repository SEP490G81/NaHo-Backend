package org.naho.vocabulary.dto.response;

import java.util.List;

public record VocabulariesOfTopicResponse(
        Long topicId,
        List<VocabularyResponse> vocabularies
) {
}
