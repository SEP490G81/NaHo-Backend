package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.RandomVocabularyPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RandomVocabularyAdapter implements RandomVocabularyPort {

    private final VocabularyJpaRepository vocabularyJpaRepository;

    @Override
    public Optional<Vocabulary> findRandomVocabulary() {
        return vocabularyJpaRepository.findRandomVocabulary()
                .map(entity -> Vocabulary.builder()
                        .id(entity.getId())
                        .reading(entity.getReading())
                        .japanese(entity.getJapanese())
                        .vietnameseMeaningText(entity.getVietnameseMeaningText())
                        .englishMeaningText(entity.getEnglishMeaningText())
                        .build());
    }

    @Override
    public List<String> findRandomDistractorMeanings(Long targetId, String targetMeaning, int limit) {
        return vocabularyJpaRepository.findRandomDistractorMeanings(targetId, targetMeaning, limit);
    }
}
