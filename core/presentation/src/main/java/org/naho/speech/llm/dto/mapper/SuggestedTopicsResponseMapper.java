package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.SuggestedTopicsResult;
import org.naho.speech.llm.dto.response.SuggestedTopicsResponse;

@Mapper(componentModel = "spring")
public interface SuggestedTopicsResponseMapper {
    SuggestedTopicsResponse resultToResponse(SuggestedTopicsResult result);

    SuggestedTopicsResponse.TopicItem resultToResponseItem(SuggestedTopicsResult.TopicItem resultItem);
}
