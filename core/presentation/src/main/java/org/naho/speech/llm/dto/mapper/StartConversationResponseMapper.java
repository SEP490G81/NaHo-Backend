package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.StartConversationResponse;
import org.naho.speech.llm.result.StartConversationResult;

@Mapper(componentModel = "spring")
public interface StartConversationResponseMapper {
    StartConversationResponse resultToResponse(StartConversationResult result);
}
