package org.naho.speech.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.azure.dto.response.WordAssessmentResponse;
import org.naho.speech.azure.result.WordAssessmentResult;

@Mapper(componentModel = "spring")
public interface WordAssessmentResponseMapper {
    WordAssessmentResponse resultToResponse(WordAssessmentResult result);
}
