package org.naho.speech.llm.question.mapper;

import org.naho.speech.llm.model.question.UsedVocabularyAndGrammar;
import org.naho.speech.llm.question.result.UsedVocabularyAndGrammarResult;

public class UsedVocabularyAndGrammarResultMapper {

    public UsedVocabularyAndGrammarResult domainToResult(UsedVocabularyAndGrammar domain) {
        if (domain == null) {
            return null;
        }
        return UsedVocabularyAndGrammarResult.builder()
                .id(domain.getId())
                .aiFeedbackId(domain.getAiFeedbackId())
                .expression(domain.getExpression())
                .category(domain.getCategory())
                .build();
    }
}
