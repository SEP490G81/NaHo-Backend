package org.naho.speech.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.shared.mapper.JsonMapper;
import org.naho.speech.topic.dto.response.CreateTopicResponse;
import org.naho.speech.topic.result.CreateTopicResult;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface TopicResponseMapper {

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    CreateTopicResponse resultToResponse(CreateTopicResult result);

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    org.naho.speech.topic.dto.response.TopicListItemResponse listItemResultToResponse(org.naho.speech.topic.result.TopicListItemResult result);

    java.util.List<org.naho.speech.topic.dto.response.TopicListItemResponse> listResultToResponse(java.util.List<org.naho.speech.topic.result.TopicListItemResult> results);
}
