package org.naho.speech.azure.port.in;

import org.naho.speech.azure.command.SpeechAssessmentCommand;
import org.naho.speech.azure.result.SpeechAssessmentResult;

public interface AssessSpeechInputPort {
    SpeechAssessmentResult execute(SpeechAssessmentCommand request);
}