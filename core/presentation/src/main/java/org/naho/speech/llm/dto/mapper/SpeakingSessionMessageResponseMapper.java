package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.SpeakingSessionMessageResult;
import org.naho.speech.llm.dto.response.SpeakingSessionMessageResponse;

@Mapper(componentModel = "spring")
public interface SpeakingSessionMessageResponseMapper {
    SpeakingSessionMessageResponse resultToResponse(SpeakingSessionMessageResult result);
}
