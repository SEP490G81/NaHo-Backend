package org.naho.speech.llm.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.entity.SpeakingSessionEntity;
import org.naho.speech.llm.model.SpeakingSession;

@Mapper(componentModel = "spring")
public interface SpeakingSessionEntityMapper {
    SpeakingSession entityToDomain(SpeakingSessionEntity entity);
}
