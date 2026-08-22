package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.SpeakingImprovedExpressionResponse;
import org.naho.speech.llm.conversation.result.SpeakingImprovedExpressionResult;

@Mapper(componentModel = "spring")
public interface SpeakingImprovedExpressionResponseMapper {
    SpeakingImprovedExpressionResponse resultToResponse(SpeakingImprovedExpressionResult result);
}
