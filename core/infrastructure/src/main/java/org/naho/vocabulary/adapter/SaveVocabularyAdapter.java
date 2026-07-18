package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveVocabularyAdapter implements SaveVocabularyPort {

    private final VocabularyJpaRepository vocabularyJpaRepository;

    @Override
    public void saveAll(List<Vocabulary> vocabularies) {
        for (Vocabulary v : vocabularies) {
            VocabularyEntity entity = new VocabularyEntity();
            entity.setReading(v.getReading());
            entity.setJapanese(v.getJapanese());
            entity.setVietnameseMeaningText(v.getVietnameseMeaningText());
            entity.setEnglishMeaningText(v.getEnglishMeaningText());

            vocabularyJpaRepository.save(entity);
        }
    }
}
