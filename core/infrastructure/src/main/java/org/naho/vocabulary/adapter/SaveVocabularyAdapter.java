package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SaveVocabularyAdapter implements SaveVocabularyPort {

    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;

    @Override
    public void saveAll(List<Vocabulary> vocabularies) {
        Map<Long, List<VocabularyEntity>> questionToVocabsMap = new HashMap<>();

        for (Vocabulary v : vocabularies) {
            VocabularyEntity entity = new VocabularyEntity();
            entity.setReading(v.getReading());
            entity.setJapanese(v.getJapanese());
            entity.setVietnameseMeaningText(v.getVietnameseMeaningText());
            entity.setEnglishMeaningText(v.getEnglishMeaningText());

            VocabularyEntity saved = vocabularyJpaRepository.save(entity);

            if (v.getQuestionId() != null) {
                questionToVocabsMap
                        .computeIfAbsent(v.getQuestionId(), k -> new ArrayList<>())
                        .add(saved);
            }
        }

        for (Map.Entry<Long, List<VocabularyEntity>> entry : questionToVocabsMap.entrySet()) {
            Long questionId = entry.getKey();
            List<VocabularyEntity> vocabsForQuestion = entry.getValue();

            Optional<VocabularyQuestionEntity> questionEntityOpt =
                    vocabularyQuestionJpaRepository.findById(questionId);

            if (questionEntityOpt.isPresent()) {
                VocabularyQuestionEntity questionEntity = questionEntityOpt.get();
                questionEntity.getVocabularies().addAll(vocabsForQuestion);
                vocabularyQuestionJpaRepository.save(questionEntity);
            }
        
        }
    }
}
