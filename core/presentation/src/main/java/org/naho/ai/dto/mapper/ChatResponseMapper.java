package org.naho.ai.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.ai.dto.response.ChatResponse;
import org.naho.ai.result.ChatResult;

@Mapper(componentModel = "spring")
public interface ChatResponseMapper {
    ChatResponse resultToResponse(ChatResult result);
}
