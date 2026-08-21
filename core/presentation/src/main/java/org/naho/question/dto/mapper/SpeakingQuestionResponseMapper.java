package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.grammar.mapper.GrammarAdminMapper;
import org.naho.question.dto.response.SpeakingQuestionResponse;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.vocabulary.dto.mapper.VocabularyResponseMapper;

@Mapper(componentModel = "spring", uses = {
        VocabularyResponseMapper.class,
        GrammarAdminMapper.class
})
public interface SpeakingQuestionResponseMapper {
    SpeakingQuestionResponse resultToResponse(SpeakingQuestionResult result);
}
