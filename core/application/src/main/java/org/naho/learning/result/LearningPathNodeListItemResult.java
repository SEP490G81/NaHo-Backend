package org.naho.learning.result;

import org.naho.learning.type.NodeType;

public record LearningPathNodeListItemResult(
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
