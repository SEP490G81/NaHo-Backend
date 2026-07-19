package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.question.model.VocabularyQuestion;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.question.port.out.VocabularyQuestionRepositoryPort;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.model.Vocabulary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VocabularyQuestionRepositoryAdapter implements VocabulariesQuestionPort, VocabularyQuestionRepositoryPort {

    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;
    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;

    @Override
    public List<Vocabulary> findVocabularyListOfObjective(int objectiveId) {
        List<LearningPathNodeEntity> nodes = learningPathNodeJpaRepository
                .findAllByObjectiveId((long) objectiveId);
        return nodes.stream()
                .map(LearningPathNodeEntity::getVocabularyQuestion)
                .filter(Objects::nonNull)
                .flatMap(vq -> vq.getVocabularies().stream())
                .map(entity -> Vocabulary.builder()
                        .id(entity.getId())
                        .reading(entity.getReading())
                        .japanese(entity.getJapanese())
                        .vietnameseMeaningText(entity.getVietnameseMeaningText())
                        .englishMeaningText(entity.getEnglishMeaningText())
                        .build())
                .toList();
    }

    @Override
    public Optional<VocabularyQuestion> findById(Long id) {
        return vocabularyQuestionJpaRepository.findById(id).map(entity -> VocabularyQuestion.builder()
                .id(entity.getId())
                .vocabularies(entity.getVocabularies().stream()
                        .map(v -> Vocabulary.builder()
                                .id(v.getId())
                                .reading(v.getReading())
                                .japanese(v.getJapanese())
                                .vietnameseMeaningText(v.getVietnameseMeaningText())
                                .englishMeaningText(v.getEnglishMeaningText())
                                .build())
                        .toList())
                .build());
    }
}
