package org.naho.speech.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.shared.mapper.JsonMapper;
import org.naho.speech.topic.dto.response.TopicResponse;
import org.naho.speech.topic.result.TopicResult;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface TopicResponseMapper {

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    TopicResponse resultToResponse(TopicResult result);
}
