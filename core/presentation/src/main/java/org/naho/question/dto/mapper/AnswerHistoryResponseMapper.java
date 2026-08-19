package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.question.dto.request.AnswerHistoryResponse;
import org.naho.question.result.AnswerHistoryResult;
import org.naho.speech.llm.question.dto.mapper.AiFeedbackResponseMapper;
import org.naho.speech.llm.question.dto.mapper.UsedVocabularyAndGrammarResponseMapper;
import org.naho.speech.llm.question.dto.mapper.UserAnswerErrorResponseMapper;

@Mapper(componentModel = "spring", uses = {
        FileResponseMapper.class,
        UserAnswerErrorResponseMapper.class,
        AiFeedbackResponseMapper.class,
        UsedVocabularyAndGrammarResponseMapper.class
})
public interface AnswerHistoryResponseMapper {
    AnswerHistoryResponse resultToResponse(AnswerHistoryResult result);
}
