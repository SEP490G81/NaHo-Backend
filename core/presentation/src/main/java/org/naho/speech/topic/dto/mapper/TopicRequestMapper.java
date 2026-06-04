package org.naho.speech.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.shared.mapper.JsonMapper;
import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.dto.request.CreateTopicRequest;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface TopicRequestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "nameTokens", source = "request.nameTokens", qualifiedByName = "convertObjectToJsonString")
    @Mapping(target = "descriptionTokens", source = "request.descriptionTokens", qualifiedByName = "convertObjectToJsonString")
    CreateTopicCommand toCommand(CreateTopicRequest request, Long userId);
}
