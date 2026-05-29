package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.SuggestedTopicsResponse;
import org.naho.speech.llm.result.SuggestedTopicsResult;

@Mapper(componentModel = "spring")
public interface SuggestedTopicsResponseMapper {
    SuggestedTopicsResponse resultToResponse(SuggestedTopicsResult result);

    SuggestedTopicsResponse.TopicItem resultToResponseItem(SuggestedTopicsResult.TopicItem resultItem);
}
