package org.naho.vocabulary.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.vocabulary.dto.response.VocabularyQuizOptionResponse;
import org.naho.vocabulary.dto.response.VocabularyQuizResponse;
import org.naho.vocabulary.result.VocabularyQuizResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VocabularyQuizResponseMapper {

    VocabularyQuizResponse toResponse(VocabularyQuizResult result);

    List<VocabularyQuizResponse> toResponseList(List<VocabularyQuizResult> results);

    VocabularyQuizOptionResponse toOptionResponse(VocabularyQuizResult.QuizOptionResult optionResult);
}
