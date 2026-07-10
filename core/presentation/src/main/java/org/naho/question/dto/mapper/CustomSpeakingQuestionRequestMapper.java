package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.command.SuggestCustomSpeakingQuestionCommand;
import org.naho.question.dto.request.SuggestCustomSpeakingQuestionRequest;

@Mapper(componentModel = "spring")
public interface CustomSpeakingQuestionRequestMapper {
    SuggestCustomSpeakingQuestionCommand requestToCommand(SuggestCustomSpeakingQuestionRequest request);
}
