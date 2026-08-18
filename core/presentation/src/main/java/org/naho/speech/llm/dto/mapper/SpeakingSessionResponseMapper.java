package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.dto.response.SpeakingSessionResponse;

@Mapper(componentModel = "spring", uses = {SpeakingSessionMessageResponseMapper.class})
public interface SpeakingSessionResponseMapper {
    SpeakingSessionResponse resultToResponse(SpeakingSessionResult result);
}
