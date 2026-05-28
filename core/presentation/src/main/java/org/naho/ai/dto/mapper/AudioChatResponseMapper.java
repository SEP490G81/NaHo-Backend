package org.naho.ai.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.ai.dto.response.AudioChatResponse;
import org.naho.ai.result.AudioChatResult;

@Mapper(componentModel = "spring")
public interface AudioChatResponseMapper {
    AudioChatResponse resultToResponse(AudioChatResult result);
}
