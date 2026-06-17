package org.naho.topic.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.topic.dto.response.CreateTopicResponse;
import org.naho.topic.dto.response.TopicDetailResponse;
import org.naho.topic.dto.response.TopicListItemResponse;
import org.naho.topic.result.CreateTopicResult;
import org.naho.topic.result.TopicDetailResult;
import org.naho.topic.result.TopicListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicResponseMapper {

    CreateTopicResponse createResultToResponse(CreateTopicResult result);

    TopicListItemResponse listItemResultToResponse(TopicListItemResult result);

    List<TopicListItemResponse> listResultToResponse(List<TopicListItemResult> results);

    TopicDetailResponse detailResultToResponse(TopicDetailResult result);
}
