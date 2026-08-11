package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.question.mapper.VocabularyEntityMapper;
import org.naho.question.mapper.VocabularyQuestionEntityMapper;
import org.naho.question.model.VocabularyQuestion;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.model.Vocabulary;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class VocabularyQuestionRepositoryAdapter implements VocabulariesQuestionPort, VocabularyQuestionRepositoryPort {

    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;
    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;
    private final VocabularyEntityMapper vocabularyEntityMapper;
    private final VocabularyQuestionEntityMapper vocabularyQuestionEntityMapper;

    @Override
    public List<Vocabulary> findVocabularyListOfObjective(int objectiveId) {
        List<LearningPathNodeEntity> nodes = learningPathNodeJpaRepository
                .findAllByObjectiveId((long) objectiveId);
        return nodes.stream()
                .map(LearningPathNodeEntity::getVocabularyQuestion)
                .filter(Objects::nonNull)
                .flatMap(vq -> vq.getVocabularies().stream())
                .map(vocabularyEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Vocabulary> findVocabularyListOfTopic(Long topicId) {
        List<LearningPathNodeEntity> nodes = learningPathNodeJpaRepository
                .findAllByObjective_Lesson_Topic_Id(topicId);

        Map<Long, Vocabulary> vocabMap = new LinkedHashMap<>();
        for (LearningPathNodeEntity node : nodes) {
            if (node.getVocabularyQuestion() != null && node.getVocabularyQuestion().getVocabularies() != null) {
                for (var entity : node.getVocabularyQuestion().getVocabularies()) {
                    vocabMap.putIfAbsent(entity.getId(), vocabularyEntityMapper.entityToDomain(entity));
                }
            }
        }
        return new ArrayList<>(vocabMap.values());
    }

    @Override
    public Optional<VocabularyQuestion> findById(Long id) {
        return vocabularyQuestionJpaRepository.findById(id)
                .map(vocabularyQuestionEntityMapper::entityToDomain);
    }
}
