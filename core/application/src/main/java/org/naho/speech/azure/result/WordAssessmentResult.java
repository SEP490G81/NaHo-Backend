package org.naho.speech.azure.result;

import org.naho.speech.azure.type.SpeechAssessmentErrorType;

public record WordAssessmentResult(
        Long id,
        String word,
        Double accuracyScore,
        SpeechAssessmentErrorType errorType
) {
}
