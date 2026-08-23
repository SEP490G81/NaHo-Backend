package org.naho.speech.llm.conversation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                SpeakingSessionIdMapper.class
        }
)
public interface SpeakingSessionAssessmentEntityMapper {

    @Mapping(target = "speakingSessionId", source = "speakingSession.id")
    SpeakingSessionAssessment entityToDomain(SpeakingSessionAssessmentEntity entity);

    @Mapping(target = "speakingSession", source = "speakingSessionId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeakingSessionAssessmentEntity domainToEntity(SpeakingSessionAssessment domain);
}
