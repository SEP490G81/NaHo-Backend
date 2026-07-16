package org.naho.learning.result;

import org.naho.learning.type.NodeType;

public record LearningPathNodeDetailResult(
        Long id,
        Long objectiveId,
        NodeType nodeType,
        Double globalOrderIndex,
        Double orderIndex,
        SpeakingQuestionDetailResult speakingQuestion,
        VocabularyQuestionDetailResult vocabularyQuestion,
        ChestDetailResult chest
) {
}
