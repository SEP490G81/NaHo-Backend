package org.naho.speech.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.azure.dto.response.PronunciationAssessmentResponse;
import org.naho.speech.azure.result.SpeechAssessmentResult;

@Mapper(componentModel = "spring")
public interface PronunciationAssessmentMapper {
    PronunciationAssessmentResponse resultToResponse(SpeechAssessmentResult result);
}
