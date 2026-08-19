package org.naho.speech.llm.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.question.dto.response.AiFeedbackResponse;
import org.naho.speech.llm.question.result.AiFeedbackResult;

@Mapper(
        componentModel = "spring",
        uses = {
                UsedVocabularyAndGrammarResponseMapper.class,
                UserAnswerErrorResponseMapper.class
        }
)
public interface AiFeedbackResponseMapper {
    AiFeedbackResponse resultToResponse(AiFeedbackResult result);
}
