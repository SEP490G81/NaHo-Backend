package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionMessageResponse;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;

@Mapper(componentModel = "spring")
public interface SpeakingSessionMessageResponseMapper {
    SpeakingSessionMessageResponse resultToResponse(SpeakingSessionMessageResult result);
}
