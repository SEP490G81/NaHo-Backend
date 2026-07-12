package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.book.dto.response.ObjectiveDetailResponse;
import org.naho.book.result.ObjectiveDetailResult;
import org.naho.question.dto.response.QuestionListItemResponse;
import org.naho.question.result.QuestionListItemResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectiveResponseMapper {

    ObjectiveDetailResponse detailResultToResponse(ObjectiveDetailResult result);

    QuestionListItemResponse questionResultToResponse(QuestionListItemResult result);

    List<QuestionListItemResponse> questionResultListToResponse(List<QuestionListItemResult> results);
}
