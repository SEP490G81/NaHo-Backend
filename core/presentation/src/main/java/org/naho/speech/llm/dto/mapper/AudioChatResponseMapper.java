package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.AudioChatResult;
import org.naho.speech.llm.dto.response.AudioChatResponse;

@Mapper(componentModel = "spring")
public interface AudioChatResponseMapper {
    AudioChatResponse resultToResponse(AudioChatResult result);
}
