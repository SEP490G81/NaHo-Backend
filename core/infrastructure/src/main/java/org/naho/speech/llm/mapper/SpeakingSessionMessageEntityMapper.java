package org.naho.speech.llm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.speech.llm.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.model.SpeakingSessionMessage;

@Mapper(componentModel = "spring")
public interface SpeakingSessionMessageEntityMapper {
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    SpeakingSessionMessage entityToDomain(SpeakingSessionMessageEntity entity);
}
