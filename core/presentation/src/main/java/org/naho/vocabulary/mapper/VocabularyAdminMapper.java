package org.naho.vocabulary.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.command.CreateVocabularyCommand;
import org.naho.vocabulary.command.UpdateVocabularyCommand;
import org.naho.vocabulary.dto.request.CreateVocabularyRequest;
import org.naho.vocabulary.dto.request.UpdateVocabularyRequest;
import org.naho.vocabulary.dto.response.VocabularyResponse;
import org.naho.vocabulary.result.VocabularyResult;

@Mapper(componentModel = "spring")
public interface VocabularyAdminMapper {
    CreateVocabularyCommand toCommand(CreateVocabularyRequest request);
    UpdateVocabularyCommand toCommand(UpdateVocabularyRequest request);
    VocabularyResponse toResponse(VocabularyResult result);
}
