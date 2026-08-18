package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.StartConversationResult;
import org.naho.speech.llm.dto.response.StartConversationResponse;

@Mapper(componentModel = "spring")
public interface StartConversationResponseMapper {
    StartConversationResponse resultToResponse(StartConversationResult result);
}
