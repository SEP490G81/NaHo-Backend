package org.naho.question.result;

import org.naho.vocabulary.model.Vocabulary;

import java.util.List;

public record VocabulariesOfQuestionResult(
        int node_id,
        int vocabulary_question_id,
        List<VocabularyDetailResult> vocabularyDetailResults

) {
    public record VocabularyDetailResult(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}


