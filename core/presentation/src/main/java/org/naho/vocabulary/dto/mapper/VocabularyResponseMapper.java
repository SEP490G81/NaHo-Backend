package org.naho.vocabulary.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.dto.response.VocabularyResponse;
import org.naho.vocabulary.result.VocabularyResult;

@Mapper(componentModel = "spring", uses = {
        VocabularyObjectiveResponseMapper.class,
})
public interface VocabularyResponseMapper {
    VocabularyResponse resultToResponse(VocabularyResult result);
}
