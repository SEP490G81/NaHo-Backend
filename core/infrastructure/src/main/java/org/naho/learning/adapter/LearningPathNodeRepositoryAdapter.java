package org.naho.learning.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.model.LearningPathNode;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LearningPathNodeRepositoryAdapter implements LearningPathNodeRepositoryPort {

    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;

    @Override
    public List<LearningPathNode> findByObjectiveId(Long objectiveId) {
        return learningPathNodeJpaRepository.findAllByObjectiveId(objectiveId).stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public Optional<LearningPathNode> findById(Long id) {
        return learningPathNodeJpaRepository.findById(id).map(this::toModel);
    }

    private LearningPathNode toModel(LearningPathNodeEntity entity) {
        return LearningPathNode.builder()
                .id(entity.getId())
                .objectiveId(entity.getObjective() != null ? entity.getObjective().getId() : null)
                .speakingQuestionId(entity.getSpeakingQuestion() != null ? entity.getSpeakingQuestion().getId() : null)
                .vocabularyQuestionId(
                        entity.getVocabularyQuestion() != null ? entity.getVocabularyQuestion().getId() : null)
                .chestId(entity.getChest() != null ? entity.getChest().getId() : null)
                .globalOrderIndex(entity.getGlobalOrderIndex())
                .orderIndex(entity.getOrderIndex())
                .nodeType(entity.getNodeType())
                .build();
    }
}
