package org.naho.speech.llm.question.mapper;

import org.naho.speech.llm.model.question.AiFeedback;
import org.naho.speech.llm.question.result.AiFeedbackResult;
import org.naho.speech.llm.question.result.UsedVocabularyAndGrammarResult;
import org.naho.speech.llm.question.result.UserAnswerErrorResult;

import java.util.ArrayList;
import java.util.List;

public class AiFeedbackResultMapper {
    private final UsedVocabularyAndGrammarResultMapper usedVocabularyAndGrammarResultMapper;
    private final UserAnswerErrorResultMapper userAnswerErrorResultMapper;

    public AiFeedbackResultMapper(
            UsedVocabularyAndGrammarResultMapper usedVocabularyAndGrammarResultMapper,
            UserAnswerErrorResultMapper userAnswerErrorResultMapper
    ) {
        this.usedVocabularyAndGrammarResultMapper = usedVocabularyAndGrammarResultMapper;
        this.userAnswerErrorResultMapper = userAnswerErrorResultMapper;
    }

    public AiFeedbackResult domainToResult(AiFeedback domain) {
        if (domain == null) {
            return null;
        }

        List<UsedVocabularyAndGrammarResult> usedVocabularyAndGrammarResultList = new ArrayList<>();

        if (domain.getUsedVocabulariesAndGrammars() != null) {
            usedVocabularyAndGrammarResultList = domain.getUsedVocabulariesAndGrammars()
                    .stream()
                    .map(usedVocabularyAndGrammarResultMapper::domainToResult)
                    .toList();
        }

        List<UserAnswerErrorResult> userAnswerErrorResultList = new ArrayList<>();

        if (domain.getUserAnswerErrors() != null) {
            userAnswerErrorResultList = domain.getUserAnswerErrors()
                    .stream()
                    .map(userAnswerErrorResultMapper::domainToResult)
                    .toList();
        }

        return AiFeedbackResult.builder()
                .id(domain.getId())
                .grammarScore(domain.getGrammarScore())
                .vocabularyScore(domain.getVocabularyScore())
                .naturalnessScore(domain.getNaturalnessScore())
                .contentRelevantScore(domain.getContentRelevantScore())
                .averageScore(domain.getAverageScore())
                .suggestJapaneseAnswer(domain.getSuggestJapaneseAnswer())
                .suggestAnswerTranslation(domain.getSuggestAnswerTranslation())
                .usedVocabulariesAndGrammars(usedVocabularyAndGrammarResultList)
                .userAnswerErrors(userAnswerErrorResultList)
                .build();
    }
}
