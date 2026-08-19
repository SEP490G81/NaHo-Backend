package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.AudioChatResponse;
import org.naho.speech.llm.conversation.result.AudioChatResult;

@Mapper(componentModel = "spring")
public interface AudioChatResponseMapper {
    AudioChatResponse resultToResponse(AudioChatResult result);
}
