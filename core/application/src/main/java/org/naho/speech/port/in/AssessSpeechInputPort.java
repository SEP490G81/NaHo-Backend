package org.naho.speech.port.in;

import org.naho.speech.dto.SpeechAssessmentRequest;
import org.naho.speech.dto.SpeechAssessmentResponse;

public interface AssessSpeechInputPort {
    SpeechAssessmentResponse execute(SpeechAssessmentRequest request);
}