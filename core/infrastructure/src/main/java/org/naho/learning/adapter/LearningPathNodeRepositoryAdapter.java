package org.naho.learning.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.mapper.LearningPathNodeEntityMapper;
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
    private final LearningPathNodeEntityMapper learningPathNodeEntityMapper;

    @Override
    public List<LearningPathNode> findByObjectiveId(Long objectiveId) {
        return learningPathNodeJpaRepository.findAllByObjectiveId(objectiveId).stream()
                .map(learningPathNodeEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<LearningPathNode> findById(Long id) {
        return learningPathNodeJpaRepository
                .findById(id)
                .map(learningPathNodeEntityMapper::entityToDomain);
    }

    @Override
    public Optional<LearningPathNode> findFirstLearningPathNode() {
        return learningPathNodeJpaRepository
                .findFirstByOrderByGlobalOrderIndexAsc()
                .map(learningPathNodeEntityMapper::entityToDomain);
    }

    @Override
    public Optional<LearningPathNode> findByChestId(Long chestId) {
        return learningPathNodeJpaRepository
                .findByChestId(chestId)
                .map(learningPathNodeEntityMapper::entityToDomain);
    }

    @Override
    public Optional<LearningPathNode> findBySpeakingQuestionId(Long speakingQuestionId) {
        return learningPathNodeJpaRepository
                .findBySpeakingQuestion_Id(speakingQuestionId)
                .map(learningPathNodeEntityMapper::entityToDomain);
    }
}
