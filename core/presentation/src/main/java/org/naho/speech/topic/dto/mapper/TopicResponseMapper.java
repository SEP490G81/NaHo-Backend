package org.naho.speech.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.shared.mapper.JsonMapper;
import org.naho.speech.topic.dto.response.CreateTopicResponse;
import org.naho.speech.topic.dto.response.TopicDetailResponse;
import org.naho.speech.topic.dto.response.TopicListItemResponse;
import org.naho.speech.topic.result.CreateTopicResult;
import org.naho.speech.topic.result.TopicDetailResult;
import org.naho.speech.topic.result.TopicListItemResult;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface TopicResponseMapper {

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    CreateTopicResponse resultToResponse(CreateTopicResult result);

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    TopicListItemResponse listItemResultToResponse(TopicListItemResult result);

    List<TopicListItemResponse> listResultToResponse(List<TopicListItemResult> results);

    @Mapping(target = "japaneseNameTokens", source = "japaneseNameTokens", qualifiedByName = "stringToObject")
    @Mapping(target = "japaneseDescriptionTokens", source = "japaneseDescriptionTokens", qualifiedByName = "stringToObject")
    TopicDetailResponse detailResultToResponse(TopicDetailResult result);
}
