package org.naho.vocabulary.dto.response;

import java.util.List;

public record VocabulariesOfObjectiveResponse(
        Long objectiveId,
        List<VocabularyResponse> vocabularies
) {
}
