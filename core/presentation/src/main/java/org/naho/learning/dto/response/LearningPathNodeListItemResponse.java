package org.naho.learning.dto.response;

import org.naho.learning.type.NodeType;

public record LearningPathNodeListItemResponse(
        Long id,
        Long objectiveId,
        Long speakingQuestionId,
        Long vocabularyQuestionId,
        Long chestId,
        Double globalOrderIndex,
        Double orderIndex,
        NodeType nodeType
) {
}
