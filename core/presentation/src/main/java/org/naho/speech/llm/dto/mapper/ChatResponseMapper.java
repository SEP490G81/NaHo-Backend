package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.ChatResponse;
import org.naho.speech.llm.result.ChatResult;

@Mapper(componentModel = "spring")
public interface ChatResponseMapper {
    ChatResponse resultToResponse(ChatResult result);
}
