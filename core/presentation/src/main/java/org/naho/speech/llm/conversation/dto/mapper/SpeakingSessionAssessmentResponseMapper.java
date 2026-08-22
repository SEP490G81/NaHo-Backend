package org.naho.speech.llm.conversation.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.speech.llm.conversation.dto.response.SpeakingSessionAssessmentResponse;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;

@Mapper(componentModel = "spring", uses = {SpeakingImprovedExpressionResponseMapper.class})
public interface SpeakingSessionAssessmentResponseMapper {
    SpeakingSessionAssessmentResponse resultToResponse(SpeakingSessionAssessmentResult result);
}
