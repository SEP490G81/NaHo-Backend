package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.dto.response.ObjectiveDetailResponse;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.question.dto.response.SpeakingQuestionListItemResponse;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectiveResponseMapper {

    ObjectiveDetailResponse detailResultToResponse(ObjectiveDetailResult result);

    @Mapping(target = "createdTime", ignore = true)
    SpeakingQuestionListItemResponse questionResultToResponse(SpeakingQuestionListItemResult result);

    List<SpeakingQuestionListItemResponse> questionResultListToResponse(List<SpeakingQuestionListItemResult> results);
}
