package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.dto.response.SpeakingQuestionResponse;
import org.naho.question.result.SpeakingQuestionResult;

@Mapper(componentModel = "spring")
public interface SpeakingQuestionResponseMapper {
    SpeakingQuestionResponse resultToResponse(SpeakingQuestionResult result);
}
