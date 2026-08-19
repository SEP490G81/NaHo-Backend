package org.naho.vocabulary.mapper;

import org.naho.question.model.Vocabulary;
import org.naho.vocabulary.result.VocabularyResult;

public class VocabularyResultMapper {
    public VocabularyResult domainToResult(Vocabulary domain) {
        if (domain == null) {
            return null;
        }

        return VocabularyResult.builder()
                .id(domain.getId())
                .reading(domain.getReading())
                .japanese(domain.getJapanese())
                .vietnameseMeaningText(domain.getVietnameseMeaningText())
                .englishMeaningText(domain.getEnglishMeaningText())
                .build();
    }
}
