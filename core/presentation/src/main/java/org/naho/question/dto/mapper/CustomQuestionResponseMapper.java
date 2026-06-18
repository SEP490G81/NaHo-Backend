package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.dto.response.SuggestCustomQuestionResponse;
import org.naho.question.result.SuggestCustomQuestionResult;

@Mapper(componentModel = "spring")
public interface CustomQuestionResponseMapper {
    SuggestCustomQuestionResponse resultToResponse(SuggestCustomQuestionResult result);
}
