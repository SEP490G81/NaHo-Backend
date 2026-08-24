package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.ChatResponse;
import org.naho.speech.llm.conversation.result.ChatResult;

@Mapper(componentModel = "spring", uses = {
        SpeakingSessionMessageResponseMapper.class,
})
public interface ChatResponseMapper {
    ChatResponse resultToResponse(ChatResult result);
}
