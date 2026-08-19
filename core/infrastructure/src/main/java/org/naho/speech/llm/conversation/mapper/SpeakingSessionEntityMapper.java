package org.naho.speech.llm.conversation.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;
import org.naho.speech.llm.model.conversation.SpeakingSession;

@Mapper(componentModel = "spring")
public interface SpeakingSessionEntityMapper {
    SpeakingSession entityToDomain(SpeakingSessionEntity entity);
}
