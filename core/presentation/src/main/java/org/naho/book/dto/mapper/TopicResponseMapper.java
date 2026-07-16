package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.CreateTopicResponse;
import org.naho.book.dto.response.LessonResponse;
import org.naho.book.dto.response.TopicDetailResponse;
import org.naho.book.dto.response.TopicResponse;
import org.naho.book.result.CreateTopicResult;
import org.naho.book.result.LessonListItemResult;
import org.naho.book.result.TopicDetailResult;
import org.naho.book.result.TopicResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicResponseMapper {

    CreateTopicResponse createResultToResponse(CreateTopicResult result);

    TopicResponse resultToResponse(TopicResult result);
    
    TopicDetailResponse detailResultToResponse(TopicDetailResult result);

    LessonResponse lessonResultToResponse(LessonListItemResult result);

    List<LessonResponse> lessonListResultToResponse(List<LessonListItemResult> results);
}
