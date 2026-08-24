package org.naho.speech.llm.conversation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.persona.mapper.PersonaIdMapper;
import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.user.mapper.UserIdMapper;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                UserIdMapper.class,
                PersonaIdMapper.class,
                SpeakingSessionAssessmentEntityMapper.class,
                SpeakingSessionMessageEntityMapper.class
        }
)
public interface SpeakingSessionEntityMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "personaId", source = "persona.id")
    SpeakingSession entityToDomain(SpeakingSessionEntity entity);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "persona", source = "personaId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeakingSessionEntity domainToEntity(SpeakingSession domain);
}
