package org.naho.speech.azure.dto.response;

import org.naho.speech.azure.type.SpeechAssessmentErrorType;

public record WordAssessmentResponse(
        Long id,
        String word,
        Double accuracyScore,
        SpeechAssessmentErrorType errorType
) {
}
