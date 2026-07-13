package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.VocabularyPort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VocabularyRepositoryAdapter implements VocabularyPort {

    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;

    @Override
    public List<Vocabulary> findVocabularyList(int vocabularyQuestionId) {
        return vocabularyQuestionJpaRepository.findById((long) vocabularyQuestionId)
                .map(VocabularyQuestionEntity::getVocabularies)
                .map(entities -> entities.stream()
                        .map(entity -> Vocabulary.builder()
                                .id(entity.getId())
                                .reading(entity.getReading())
                                .japanese(entity.getJapanese())
                                .vietnameseMeaningText(entity.getVietnameseMeaningText())
                                .englishMeaningText(entity.getEnglishMeaningText())
                                .build())
                        .toList())
                .orElse(Collections.emptyList());
    }
}
