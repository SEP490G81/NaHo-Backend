package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.dto.response.SuggestCustomSpeakingQuestionResponse;
import org.naho.question.result.SuggestCustomSpeakingQuestionResult;

@Mapper(componentModel = "spring")
public interface CustomSpeakingQuestionResponseMapper {
    SuggestCustomSpeakingQuestionResponse resultToResponse(SuggestCustomSpeakingQuestionResult result);
}
