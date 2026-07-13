package org.naho.vocabulary.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.dto.response.VocabulariesOfObjectiveResponse;
import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;

@Mapper(componentModel = "spring")
public interface VocabularyObjectiveResponseMapper {
    VocabulariesOfObjectiveResponse toResponse(VocabulariesOfObjectiveResult result);
}
