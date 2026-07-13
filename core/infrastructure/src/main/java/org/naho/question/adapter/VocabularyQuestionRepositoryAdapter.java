package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.question.port.out.VocabulariesQuestionPort;
import org.naho.vocabulary.model.Vocabulary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VocabularyQuestionRepositoryAdapter implements VocabulariesQuestionPort {

    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;

    @Override
    public List<Vocabulary> findVocabularyListOfObjective(int objectiveId) {
        List<LearningPathNodeEntity> nodes = learningPathNodeJpaRepository.findAllByObjectiveId((long) objectiveId);
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
}
