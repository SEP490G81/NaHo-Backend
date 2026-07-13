package org.naho.vocabulary.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.model.Vocabulary;
import org.naho.vocabulary.port.out.SaveVocabularyPort;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SaveVocabularyAdapter implements SaveVocabularyPort {

    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;

    @Override
    public void saveAll(List<Vocabulary> vocabularies) {
        // Group vocabularies by their questionId for bulk linking
        // questionId in the Excel = VocabularyQuestionEntity.id (the question row in DB)
        Map<Long, List<VocabularyEntity>> questionToVocabsMap = new HashMap<>();

        for (Vocabulary v : vocabularies) {
            VocabularyEntity entity = new VocabularyEntity();
            entity.setReading(v.getReading());
            entity.setJapanese(v.getJapanese());
            entity.setVietnameseMeaningText(v.getVietnameseMeaningText());
            entity.setEnglishMeaningText(v.getEnglishMeaningText());

            // Save vocabulary first to get its ID
            VocabularyEntity saved = vocabularyJpaRepository.save(entity);

            // Group by questionId if present
            if (v.getQuestionId() != null) {
                questionToVocabsMap
                        .computeIfAbsent(v.getQuestionId(), k -> new ArrayList<>())
                        .add(saved);
            }
        }

        // Link vocabularies to VocabularyQuestion (join table: vocabulary_questions_vocabularies)
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
            // If VocabularyQuestion doesn't exist in DB yet, skip linking
            // (vocabularies are already saved; can be linked later)
        }
    }
}
