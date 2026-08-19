package org.naho.speech.azure.port.in;

import org.naho.speech.azure.result.SpeechAssessmentResult;

public interface CrudSpeechAssessmentInputPort {
    SpeechAssessmentResult findById(Long id);
}
