package org.naho.speech.topic.dto.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.speech.topic.dto.response.TopicResponse;
import org.naho.speech.topic.result.TopicResult;

@Mapper(componentModel = "spring")
public interface TopicResponseMapper {

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    TopicResponse resultToResponse(TopicResult result);

    @Named("stringToObject")
    default Object stringToObject(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            return new ObjectMapper().readTree(str);
        } catch (JsonProcessingException e) {
            return str;
        }
    }
}
