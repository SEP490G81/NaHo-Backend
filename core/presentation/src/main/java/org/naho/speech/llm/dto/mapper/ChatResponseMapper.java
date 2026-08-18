package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.ChatResult;
import org.naho.speech.llm.dto.response.ChatResponse;

@Mapper(componentModel = "spring")
public interface ChatResponseMapper {
    ChatResponse resultToResponse(ChatResult result);
}
