package org.naho.speech.topic.dto.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.dto.request.CreateTopicRequest;

@Mapper(componentModel = "spring")
public interface TopicRequestMapper {

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "nameTokens", expression = "java(convertObjectToJsonString(request.nameTokens()))")
    @Mapping(target = "descriptionTokens", expression = "java(convertObjectToJsonString(request.descriptionTokens()))")
    CreateTopicCommand toCommand(CreateTopicRequest request, Long userId);

    @Named("convertObjectToJsonString")
    default String convertObjectToJsonString(Object obj) {
        if (obj == null) return null;
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
