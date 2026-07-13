package org.naho.question.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.question.dto.response.VocabulariesOfQuestionResponse;
import org.naho.question.result.VocabulariesOfQuestionResult;

@Mapper(componentModel = "spring")
public interface VocabularyQuestionResponseMapper {

    @Mapping(target = "nodeId", source = "node_id")
    @Mapping(target = "vocabularyQuestionId", source = "vocabulary_question_id")
    VocabulariesOfQuestionResponse toResponse(VocabulariesOfQuestionResult result);
}
