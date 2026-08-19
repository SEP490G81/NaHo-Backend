package org.naho.learning.dto.response;

import org.naho.chest.dto.response.ChestResponse;
import org.naho.learning.type.NodeType;
import org.naho.question.dto.response.SpeakingQuestionResponse;
import org.naho.question.dto.response.VocabularyQuestionResponse;

public record LearningPathNodeDetailResponse(
        Long id,
        Long objectiveId,
        NodeType nodeType,
        Double globalOrderIndex,
        Double orderIndex,
        SpeakingQuestionResponse speakingQuestion,
        VocabularyQuestionResponse vocabularyQuestion,
        ChestResponse chest
) {
}
