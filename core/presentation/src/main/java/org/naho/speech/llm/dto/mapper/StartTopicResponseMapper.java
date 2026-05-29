package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.StartTopicResponse;
import org.naho.speech.llm.result.SpeakingTopicResult;

@Mapper(componentModel = "spring")
public interface StartTopicResponseMapper {
    StartTopicResponse resultToResponse(SpeakingTopicResult result);
}
