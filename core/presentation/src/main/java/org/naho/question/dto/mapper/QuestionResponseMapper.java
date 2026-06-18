package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.dto.response.QuestionDetailResponse;
import org.naho.question.dto.response.QuestionListItemResponse;
import org.naho.question.result.CreateQuestionResult;
import org.naho.question.result.QuestionListItemResult;
import org.naho.question.result.UpdateQuestionResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionResponseMapper {

    QuestionDetailResponse createResultToDetailResponse(CreateQuestionResult result);

    QuestionDetailResponse updateResultToDetailResponse(UpdateQuestionResult result);

    @Mapping(target = "createdTime", ignore = true)
    QuestionListItemResponse listResultToResponse(QuestionListItemResult item);

    List<QuestionListItemResponse> listResultToResponse(List<QuestionListItemResult> items);
}
