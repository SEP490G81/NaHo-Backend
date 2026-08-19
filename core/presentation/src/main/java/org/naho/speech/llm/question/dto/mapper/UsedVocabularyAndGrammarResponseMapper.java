package org.naho.speech.llm.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.question.dto.response.UsedVocabularyAndGrammarResponse;
import org.naho.speech.llm.question.result.UsedVocabularyAndGrammarResult;

@Mapper(componentModel = "spring")
public interface UsedVocabularyAndGrammarResponseMapper {
    UsedVocabularyAndGrammarResponse resultToResponse(UsedVocabularyAndGrammarResult result);
}
