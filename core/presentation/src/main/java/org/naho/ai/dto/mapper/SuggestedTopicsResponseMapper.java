package org.naho.ai.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.ai.dto.response.SuggestedTopicsResponse;
import org.naho.ai.result.SuggestedTopicsResult;

@Mapper(componentModel = "spring")
public interface SuggestedTopicsResponseMapper {
    SuggestedTopicsResponse resultToResponse(SuggestedTopicsResult result);

    SuggestedTopicsResponse.TopicItem resultToResponseItem(SuggestedTopicsResult.TopicItem resultItem);
}
