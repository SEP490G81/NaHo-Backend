package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.SuggestedTopicsResponse;
import org.naho.speech.llm.conversation.result.SuggestedTopicsResult;

@Mapper(componentModel = "spring")
public interface SuggestedTopicsResponseMapper {
    SuggestedTopicsResponse resultToResponse(SuggestedTopicsResult result);

    SuggestedTopicsResponse.TopicItem resultToResponseItem(SuggestedTopicsResult.TopicItem resultItem);
}
