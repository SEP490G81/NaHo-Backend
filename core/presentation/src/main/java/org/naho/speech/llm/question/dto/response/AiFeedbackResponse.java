package org.naho.speech.llm.question.dto.response;

import java.util.List;

public record AiFeedbackResponse(
        Long id,
        Double grammarScore,
        Double vocabularyScore,
        Double naturalnessScore,
        Double contentRelevantScore,
        Double averageScore,
        String suggestJapaneseAnswer,
        String suggestAnswerTranslation,
        List<UsedVocabularyAndGrammarResponse> usedVocabulariesAndGrammars,
        List<UserAnswerErrorResponse> userAnswerErrors
) {
}
