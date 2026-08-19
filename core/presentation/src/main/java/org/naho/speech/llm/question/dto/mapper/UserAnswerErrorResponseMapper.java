package org.naho.speech.llm.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.question.dto.response.UserAnswerErrorResponse;
import org.naho.speech.llm.question.result.UserAnswerErrorResult;

@Mapper(componentModel = "spring")
public interface UserAnswerErrorResponseMapper {
    UserAnswerErrorResponse resultToResponse(UserAnswerErrorResult result);
}
