package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.command.SuggestCustomQuestionCommand;
import org.naho.question.dto.request.SuggestCustomQuestionRequest;

@Mapper(componentModel = "spring")
public interface CustomQuestionRequestMapper {
    SuggestCustomQuestionCommand requestToCommand(SuggestCustomQuestionRequest request);
}
