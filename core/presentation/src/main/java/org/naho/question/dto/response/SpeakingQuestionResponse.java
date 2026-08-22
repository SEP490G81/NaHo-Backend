package org.naho.question.dto.response;

import org.naho.grammar.dto.response.GrammarResponse;
import org.naho.question.type.QuestionStatus;
import org.naho.vocabulary.dto.response.VocabularyResponse;

import java.util.List;

public record SpeakingQuestionResponse(
        Long id,
        Long userId,
        List<GrammarResponse> grammars,
        List<VocabularyResponse> vocabularies,
        String japaneseName,
        String japaneseNameMarkup,
        String vietnameseName,
        String description,
        String descriptionMarkup,
        String japaneseSampleAnswer,
        String japaneseSampleAnswerMarkup,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        QuestionStatus status
) {
}
