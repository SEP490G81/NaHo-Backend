package org.naho.speech.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.dto.response.SpeakingSessionMessageResponse;
import org.naho.speech.llm.result.SpeakingSessionMessageResult;

@Mapper(componentModel = "spring")
public interface SpeakingSessionMessageResponseMapper {
    SpeakingSessionMessageResponse resultToResponse(SpeakingSessionMessageResult result);
}
