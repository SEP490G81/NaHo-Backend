package org.naho.question.result;

import org.naho.grammar.result.GrammarDetailResult;
import org.naho.question.type.QuestionStatus;
import org.naho.vocabulary.result.VocabularyDetailResult;

import java.util.List;

public record SpeakingQuestionDetailResult(
        Long id,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        String sampleAnswer,
        QuestionStatus status,
        List<VocabularyDetailResult> vocabularies,
        List<GrammarDetailResult> grammars
) {
}
