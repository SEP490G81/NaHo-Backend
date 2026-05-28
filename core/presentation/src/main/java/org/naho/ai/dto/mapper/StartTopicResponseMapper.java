package org.naho.ai.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.ai.dto.response.StartTopicResponse;
import org.naho.ai.result.SpeakingTopicResult;

@Mapper(componentModel = "spring")
public interface StartTopicResponseMapper {
    StartTopicResponse resultToResponse(SpeakingTopicResult result);
}
