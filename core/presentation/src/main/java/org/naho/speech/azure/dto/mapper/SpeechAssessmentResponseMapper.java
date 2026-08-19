package org.naho.speech.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.azure.dto.response.SpeechAssessmentResponse;
import org.naho.speech.azure.result.SpeechAssessmentResult;

@Mapper(componentModel = "spring", uses = {WordAssessmentResponseMapper.class})
public interface SpeechAssessmentResponseMapper {
    SpeechAssessmentResponse resultToResponse(SpeechAssessmentResult result);
}
