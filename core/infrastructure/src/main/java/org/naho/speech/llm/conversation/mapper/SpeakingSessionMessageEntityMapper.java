package org.naho.speech.llm.conversation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.naho.file.mapper.FileIdMapper;
import org.naho.speech.llm.conversation.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = {
                SpeakingSessionIdMapper.class,
                FileIdMapper.class
        }
)
public interface SpeakingSessionMessageEntityMapper {

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "audioFileId", source = "audioFile.id")
    SpeakingSessionMessage entityToDomain(SpeakingSessionMessageEntity entity);

    @Mapping(target = "session", source = "sessionId")
    @Mapping(target = "audioFile", source = "audioFileId")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SpeakingSessionMessageEntity domainToEntity(SpeakingSessionMessage domain);
}
