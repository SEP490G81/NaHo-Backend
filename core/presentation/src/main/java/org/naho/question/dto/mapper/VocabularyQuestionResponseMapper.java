package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.question.dto.response.VocabularyQuestionResponse;
import org.naho.vocabulary.result.VocabularyQuestionResult;

@Mapper(componentModel = "spring")
public interface VocabularyQuestionResponseMapper {
    VocabularyQuestionResponse resultToResponse(VocabularyQuestionResult result);
}
