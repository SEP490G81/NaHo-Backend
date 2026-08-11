package org.naho.vocabulary.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.dto.response.VocabulariesOfTopicResponse;
import org.naho.vocabulary.result.VocabulariesOfTopicResult;

@Mapper(componentModel = "spring")
public interface VocabularyTopicResponseMapper {
    VocabulariesOfTopicResponse toResponse(VocabulariesOfTopicResult result);
}
