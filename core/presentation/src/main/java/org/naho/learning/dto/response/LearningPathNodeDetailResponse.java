package org.naho.learning.dto.response;

import org.naho.learning.type.NodeType;
import org.naho.question.type.QuestionStatus;

import java.util.List;

public record LearningPathNodeDetailResponse(
        Long id,
        Long objectiveId,
        NodeType nodeType,
        Double globalOrderIndex,
        Double orderIndex,
        SpeakingQuestionDetailResponse speakingQuestion,
        VocabularyQuestionDetailResponse vocabularyQuestion,
        ChestDetailResponse chest
) {
    public record SpeakingQuestionDetailResponse(
            Long id,
            Long userId,
            String title,
            String titleMarkup,
            String description,
            String descriptionMarkup,
            QuestionStatus status
    ) {}

    public record VocabularyQuestionDetailResponse(
            Long id,
            List<VocabularyDetailResponse> vocabularies
    ) {
        public record VocabularyDetailResponse(
                Long id,
                String reading,
                String japanese,
                String vietnameseMeaningText,
                String englishMeaningText
        ) {}
    }

    public record ChestDetailResponse(
            Long id,
            String title,
            String description,
            Double point
    ) {}
}
