package org.naho.learning.result;

import org.naho.chest.result.ChestResult;
import org.naho.learning.type.NodeType;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.vocabulary.result.VocabularyQuestionResult;

public record LearningPathNodeDetailResult(
        Long id,
        Long objectiveId,
        NodeType nodeType,
        Double globalOrderIndex,
        Double orderIndex,
        SpeakingQuestionResult speakingQuestion,
        VocabularyQuestionResult vocabularyQuestion,
        ChestResult chest
) {
}
