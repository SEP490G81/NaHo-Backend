package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionResponse;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;

@Mapper(componentModel = "spring", uses = {SpeakingSessionMessageResponseMapper.class})
public interface SpeakingSessionResponseMapper {
    SpeakingSessionResponse resultToResponse(SpeakingSessionResult result);
}
