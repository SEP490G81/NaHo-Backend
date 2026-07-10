package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.dto.response.SpeakingQuestionDetailResponse;
import org.naho.question.dto.response.SpeakingQuestionListItemResponse;
import org.naho.question.result.CreateSpeakingQuestionResult;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.question.result.UpdateSpeakingQuestionResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpeakingQuestionResponseMapper {

    SpeakingQuestionDetailResponse createResultToDetailResponse(CreateSpeakingQuestionResult result);

    SpeakingQuestionDetailResponse updateResultToDetailResponse(UpdateSpeakingQuestionResult result);

    @Mapping(target = "createdTime", ignore = true)
    SpeakingQuestionListItemResponse listResultToResponse(SpeakingQuestionListItemResult item);

    List<SpeakingQuestionListItemResponse> listResultToResponse(List<SpeakingQuestionListItemResult> items);
}
