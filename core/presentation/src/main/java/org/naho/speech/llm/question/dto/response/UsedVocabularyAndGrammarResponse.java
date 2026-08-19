package org.naho.speech.llm.question.dto.response;

import org.naho.speech.llm.type.LanguageCategory;

public record UsedVocabularyAndGrammarResponse(
        Long id,
        Long aiFeedbackId,
        String expression,
        LanguageCategory category
) {
}
