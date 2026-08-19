package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.StartConversationResponse;
import org.naho.speech.llm.conversation.result.StartConversationResult;

@Mapper(componentModel = "spring")
public interface StartConversationResponseMapper {
    StartConversationResponse resultToResponse(StartConversationResult result);
}
