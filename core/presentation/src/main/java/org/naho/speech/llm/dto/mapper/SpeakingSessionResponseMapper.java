package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.SpeakingSessionResponse;
import org.naho.speech.llm.result.SpeakingSessionResult;

@Mapper(componentModel = "spring", uses = {SpeakingSessionMessageResponseMapper.class})
public interface SpeakingSessionResponseMapper {
    SpeakingSessionResponse resultToResponse(SpeakingSessionResult result);
}
