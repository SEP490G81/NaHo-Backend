package org.naho.question.dto.response;

import org.naho.vocabulary.dto.response.VocabularyResponse;

import java.util.List;

public record VocabularyQuestionResponse(
        Long id,
        List<VocabularyResponse> vocabularies
) {
}
